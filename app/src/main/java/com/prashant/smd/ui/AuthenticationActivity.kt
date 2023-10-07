package com.prashant.smd.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthProvider
import com.prashant.smd.R
import com.prashant.smd.databinding.ActivityAuthenticationBinding
import com.prashant.smd.firebase.Constant
import com.prashant.smd.model.UserModel
import com.prashant.smd.util.CProgressDialog
import com.prashant.smd.util.LocalPreference

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var bind: ActivityAuthenticationBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var type: String
    private lateinit var mobile: String
    private lateinit var name: String
    private lateinit var place: String

    private lateinit var mediaPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(bind.root)


        mediaPlayer = MediaPlayer.create(this, R.raw.otp_voice)
        mediaPlayer.start()



        val verificationId = intent.getStringExtra("verificationId")
        type = intent.getStringExtra("type").toString()
        mobile = intent.getStringExtra("mobile").toString()



        if (type == "new") {
            bind.newLayout.visibility = View.VISIBLE
        }

        bind.nextBtn.setOnClickListener {
            authenticateUser(verificationId)
        }

        bind.backBtn.setOnClickListener {
            finish()
        }


    }

    private fun authenticateUser(verificationId: String?) {
        CProgressDialog.mShow(this@AuthenticationActivity)
        name = bind.fullNameEd.text.toString()
        place = bind.addressEd.text.toString()
        val otp = bind.otpEd.text.toString()

        if (type == "new") {
            if (name.isEmpty()) {
                CProgressDialog.mDismiss()
                bind.fullNameEd.requestFocus()
                bind.fullNameEd.error = "Empty!"
            } else if (place.isEmpty()) {
                CProgressDialog.mDismiss()
                bind.addressEd.requestFocus()
                bind.addressEd.error = "Empty!"
            } else if (otp.isEmpty()) {
                CProgressDialog.mDismiss()
                bind.otpEd.requestFocus()
                bind.otpEd.error = "Empty!"
            } else {
                callVerification(verificationId, otp)
            }
        } else {
            if (otp.isEmpty()) {
                CProgressDialog.mDismiss()
                bind.otpEd.requestFocus()
                bind.otpEd.error = "Empty!"
            } else {
                callVerification(verificationId, otp)
            }
        }
    }

    private fun callVerification(verificationId: String?, otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)

        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                if (type == "new") {
                    val model = UserModel()

                    model.name = name
                    model.place = place
                    model.mobile = mobile
                    model.isAdmin = "false"

                    Constant.userDB.child(mobile).setValue(model).addOnCompleteListener { task1 ->
                        if (task1.isSuccessful) {
                            makeToast("Thanks for registration")
                            LocalPreference.storeUserDetail(this@AuthenticationActivity,"false", mobile)
                            navigateDashboard()
                        }
                    }.addOnFailureListener {
                        makeToast(it.toString())
                        CProgressDialog.mDismiss()
                    }
                } else {
                    navigateDashboard()
                }

            } else {
                CProgressDialog.mDismiss()
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    makeToast(task.exception.toString())
                } else {
                    makeToast("Something went wrong please try again later !")
                }
            }
        }
    }

    private fun makeToast(toString: String) {
        Toast.makeText(this@AuthenticationActivity, toString, Toast.LENGTH_SHORT).show()

    }

    private fun navigateDashboard() {
        CProgressDialog.mDismiss()
        intent = Intent(this@AuthenticationActivity, DashboardActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}