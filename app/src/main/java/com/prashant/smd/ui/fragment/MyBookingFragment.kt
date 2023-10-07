package com.prashant.smd.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.prashant.smd.adapter.BookingAdapter
import com.prashant.smd.databinding.FragmentMyBookingBinding
import com.prashant.smd.firebase.Constant
import com.prashant.smd.model.BookingModel
import com.prashant.smd.ui.BookNowActivity
import com.prashant.stockmarketadviser.firebase.AuthManager


class MyBookingFragment : Fragment() {

    private lateinit var bind: FragmentMyBookingBinding
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        bind = FragmentMyBookingBinding.inflate(inflater, container, false)
        initRecyclerView()
        setupListener()
        return bind.root
    }

    private fun setupListener() {
        bind.bookNowBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), BookNowActivity::class.java))
        }
    }

    private fun initRecyclerView() {
        val query = Constant.bookingDb.orderByChild("mobile")
            .equalTo(AuthManager.userChecker(requireActivity())?.mobile)

        val options = FirebaseRecyclerOptions.Builder<BookingModel>()
            .setQuery(query, BookingModel::class.java).build()

        bookingAdapter = BookingAdapter(options, requireActivity())

        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        bind.homeRecyclerview.layoutManager = layoutManager
        bind.homeRecyclerview.adapter = bookingAdapter

        // Hide progress and show "no data" view when there's no data
        bookingAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (bookingAdapter.itemCount == 0) {
                    bind.noDataView.visibility = View.VISIBLE
                } else {
                    bind.noDataView.visibility = View.GONE
                }
                bind.pd.visibility = View.GONE
            }
        })

        Constant.bookingDb.orderByChild("mobile")
            .equalTo(AuthManager.userChecker(requireActivity())?.mobile)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    bind.pd.visibility = View.GONE
                    bind.noDataView.visibility =
                        if (!dataSnapshot.exists()) View.VISIBLE else View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error if needed.
                }
            })
    }

    override fun onStart() {
        super.onStart()
        bookingAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        bookingAdapter.stopListening()
    }
}
