package com.prashant.smd.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.prashant.smd.R
import com.prashant.smd.databinding.ActivityDashboardBinding
import com.prashant.smd.firebase.Constant
import com.prashant.smd.ui.fragment.AccountFragment
import com.prashant.smd.ui.fragment.HomeFragment
import com.prashant.smd.ui.fragment.MyBookingFragment
import com.prashant.stockmarketadviser.firebase.AuthManager

class DashboardActivity : AppCompatActivity() {
    private lateinit var bind: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(bind.root)


        selectFragment(HomeFragment())

        var clickCount = 0

        bind.logo.setOnClickListener {
            clickCount++
            val userMobile = AuthManager.userChecker(this@DashboardActivity)?.mobile

            if (userMobile != null) {
                val userDatabaseReference = Constant.userDB.child(userMobile)

                if (clickCount == 4) {
                    // Set admin to true
                    val updates = HashMap<String, Any>()
                    updates["admin"] = "true"

                    userDatabaseReference.updateChildren(updates).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this@DashboardActivity,
                                "Congratulations! You are now an admin",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Handle update failure
                            Toast.makeText(
                                this@DashboardActivity,
                                "Admin status update failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (clickCount == 6) {
                    // Set admin to false
                    val updates = HashMap<String, Any>()
                    updates["admin"] = "false"

                    userDatabaseReference.updateChildren(updates).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this@DashboardActivity,
                                "You are no longer an admin",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Handle update failure
                            Toast.makeText(
                                this@DashboardActivity,
                                "Admin status update failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    // Reset the click count
                    clickCount = 0
                }
            }
        }

        bind.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home_fragment -> selectFragment(HomeFragment())
                R.id.menu_booking_fragment -> selectFragment(MyBookingFragment())
                R.id.menu_account_fragment -> selectFragment(AccountFragment())
            }
            true
        }
    }

    private fun selectFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }


}