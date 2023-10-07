package com.prashant.smd.firebase

import com.google.firebase.database.FirebaseDatabase

class Constant {
    companion object {
        val userDB = FirebaseDatabase.getInstance().reference.child("Users")
        val homeDB = FirebaseDatabase.getInstance().reference.child("Home")
        val bookingDb = FirebaseDatabase.getInstance().reference.child("Booking")
    }
}