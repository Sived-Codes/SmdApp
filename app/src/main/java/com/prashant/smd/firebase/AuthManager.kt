package com.prashant.stockmarketadviser.firebase

import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.prashant.smd.firebase.Constant
import com.prashant.smd.model.UserModel
import com.prashant.smd.util.LocalPreference


class AuthManager {

    companion object {
        private var model: UserModel? = null

        fun userChecker(appContext: Context): UserModel? {
            try {
                val mobile = LocalPreference.getMobile(appContext)
                if (mobile != null) {

                    Constant.userDB.child(mobile).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            model = snapshot.getValue(UserModel::class.java)
                            if (model != null) {


                                LocalPreference.storeUserDetail(appContext, userModel!!.isAdmin, userModel!!.mobile)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }
            } catch (e: Exception) {
                Log.e("AuthManager", "userChecker: " + e.message, e)
            }
            return model
        }


        val userModel: UserModel?
            get() = model

    }
}