package com.prashant.smd.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.prashant.smd.R
import com.prashant.smd.adapter.HomeImageAdapter
import com.prashant.smd.databinding.FragmentHomeBinding
import com.prashant.smd.firebase.Constant
import com.prashant.smd.model.ImageHomeModel
import com.prashant.smd.model.UserModel
import com.prashant.smd.ui.admin.AddHomeActivity
import com.prashant.stockmarketadviser.firebase.AuthManager

class HomeFragment : Fragment() {
    private lateinit var bind: FragmentHomeBinding
    private lateinit var imageAdapter: HomeImageAdapter

    private var isBhagwatFilterActive = false
    private var isShadiFilterActive = false
    private var isOtherFilterActive = false


    private lateinit var user: UserModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        bind = FragmentHomeBinding.inflate(inflater, container, false)
        user = AuthManager.userChecker(requireActivity()) ?: UserModel()

        if (user.isAdmin == "true"){
            adminFunction()
             bind.userLayout.visibility =View.GONE
             bind.adminLayout.visibility =View.VISIBLE
        }else{
            bind.userLayout.visibility =View.VISIBLE
            bind.adminLayout.visibility =View.GONE
        }

        setupUI()
        initRecyclerView()
        return bind.root
    }

    private fun adminFunction() {

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years.", ScaleTypes.FIT))
        imageList.add(SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct.",ScaleTypes.FIT))
        imageList.add(SlideModel("https://bit.ly/3fLJf72", "And people do that.", ScaleTypes.FIT))
        bind.imageSlider.setImageList(imageList)

    }

    private fun setupUI() {
        val model: UserModel? = AuthManager.userChecker(requireActivity())
        bind.addBtn.visibility = if (model?.isAdmin == "true") View.VISIBLE else View.GONE

        setButtonColor(bind.bhagwatFilterBtn, isBhagwatFilterActive)
        setButtonColor(bind.shadiFilterBtn, isShadiFilterActive)
        setButtonColor(bind.otherFilterBtn, isOtherFilterActive)


        bind.addBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), AddHomeActivity::class.java))
        }

        bind.bhagwatFilterBtn.setOnClickListener {
            filterData("Bhagwat") // Replace with your actual category name
        }

        bind.shadiFilterBtn.setOnClickListener {
            filterData("Shadi") // Replace with your actual category name
        }

        bind.otherFilterBtn.setOnClickListener {
            filterData("Other") // Replace with your actual category name
        }
    }

    private fun filterData(category: String) {
        // Toggle the filter state for the selected category
        when (category) {
            "Bhagwat" -> isBhagwatFilterActive = !isBhagwatFilterActive
            "Shadi" -> isShadiFilterActive = !isShadiFilterActive
            "Other" -> isOtherFilterActive = !isOtherFilterActive
        }

        // Set the button background color based on the filter state
        setButtonColor(bind.bhagwatFilterBtn, isBhagwatFilterActive)
        setButtonColor(bind.shadiFilterBtn, isShadiFilterActive)
        setButtonColor(bind.otherFilterBtn, isOtherFilterActive)

        // Check if any filter is active, and apply the filter accordingly
        if (isBhagwatFilterActive || isShadiFilterActive || isOtherFilterActive) {
            val filteredCategory = mutableListOf<String>()
            if (isBhagwatFilterActive) filteredCategory.add("Bhagwat")
            if (isShadiFilterActive) filteredCategory.add("Shadi")
            if (isOtherFilterActive) filteredCategory.add("Other")

            val query = Constant.homeDB.orderByChild("category").equalTo(filteredCategory[0])

            val options = FirebaseRecyclerOptions.Builder<ImageHomeModel>()
                .setQuery(query, ImageHomeModel::class.java).build()

            imageAdapter.updateOptions(options)
        } else {
            // No filters are active, show all data
            val options = FirebaseRecyclerOptions.Builder<ImageHomeModel>()
                .setQuery(Constant.homeDB, ImageHomeModel::class.java).build()

            imageAdapter.updateOptions(options)
        }
    }

    private fun setButtonColor(button: MaterialCardView, isActive: Boolean) {
        if (isActive) {
            button.setBackgroundResource(R.drawable.active_button_background)
        } else {
            button.setBackgroundResource(R.drawable.inactive_button_background)
        }
    }

    private fun initRecyclerView() {
        bind.pd.visibility = View.VISIBLE

        Constant.homeDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                bind.pd.visibility = View.GONE
                bind.noDataView.visibility = if (!dataSnapshot.exists()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        val options = FirebaseRecyclerOptions.Builder<ImageHomeModel>()
            .setQuery(Constant.homeDB, ImageHomeModel::class.java).build()

        bind.pd.visibility = View.VISIBLE


        imageAdapter = HomeImageAdapter(options, requireActivity())
        bind.homeRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        bind.homeRecyclerview.adapter = imageAdapter


    }

    override fun onStart() {
        super.onStart()
        imageAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        imageAdapter.stopListening()
    }
}
