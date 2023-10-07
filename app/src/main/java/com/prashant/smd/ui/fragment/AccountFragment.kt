package com.prashant.smd.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.prashant.smd.databinding.FragmentAccountBinding
import com.prashant.smd.model.UserModel
import com.prashant.smd.ui.LoginActivity
import com.prashant.smd.util.CProgressDialog
import com.prashant.smd.util.LocalPreference
import com.prashant.stockmarketadviser.firebase.AuthManager


class AccountFragment : Fragment() {
    private lateinit var bind: FragmentAccountBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var user: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        bind = FragmentAccountBinding.inflate(inflater, container, false)

        bind.logOutBtn.setOnClickListener {

            CProgressDialog.mShow(requireActivity())
            auth.signOut()
            LocalPreference.clearData(requireActivity())
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        setupProfile()

        return bind.root
    }

    private fun setupProfile() {
        user = AuthManager.userChecker(requireActivity()) ?: UserModel()
        bind.name.text =user.name
        bind.mobile.text =user.mobile
        bind.place.text =user.place

    }

    override fun onDestroy() {
        super.onDestroy()
        CProgressDialog.mDismiss()

    }

}