package com.prashant.smd.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.prashant.smd.R
import com.prashant.smd.databinding.ActivityLoginBinding
import com.prashant.smd.firebase.Constant
import com.prashant.smd.util.CProgressDialog
import com.prashant.smd.util.LocalPreference
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var bind: ActivityLoginBinding

    private val auth = FirebaseAuth.getInstance()

    private lateinit var mediaPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        mediaPlayer = MediaPlayer.create(this, R.raw.login_voice)

        if (LocalPreference.getLoginVoiceStatus(this@LoginActivity)!="done"){
            LocalPreference.loginVoice(this@LoginActivity, "done ")

            mediaPlayer.start()

        }

        if (auth.currentUser != null) {
            intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


        bind.continueBtn.setOnClickListener {
            CProgressDialog.mShow(this@LoginActivity)

            val getMobile = bind.userMobile.text.toString()

            if (getMobile.isEmpty()) {
                CProgressDialog.mDismiss()
                Toast.makeText(this, "Please enter mobile number !", Toast.LENGTH_SHORT).show()
            } else {

                Constant.userDB.child(getMobile)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val admin = snapshot.child("admin").getValue(String::class.java)
                                LocalPreference.storeUserDetail(this@LoginActivity, admin, getMobile)
                                userAuthentication(getMobile, "exist")
                            } else {
                                userAuthentication(getMobile, "new")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            CProgressDialog.mDismiss()

                            makeToast(error.message.toString())
                        }
                    })

            }
        }
    }

    private fun userAuthentication(mobile: String, type: String) {


        val phoneNumber = "+91$mobile"
        val options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    CProgressDialog.mDismiss()

                }

                override fun onVerificationFailed(e: FirebaseException) {
                    CProgressDialog.mDismiss()
                    makeToast(e.message.toString())

                }

                override fun onCodeSent(
                    verificationId: String, token: PhoneAuthProvider.ForceResendingToken
                ) {
                    CProgressDialog.mDismiss()
                    makeToast("OTP sent to your number $mobile")

                    val intent = Intent(this@LoginActivity, AuthenticationActivity::class.java)
                    intent.putExtra("verificationId", verificationId)
                    intent.putExtra("type", type)
                    intent.putExtra("mobile", mobile)
                    startActivity(intent)
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun makeToast(toString: String) {
        Toast.makeText(this@LoginActivity, toString, Toast.LENGTH_SHORT).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}