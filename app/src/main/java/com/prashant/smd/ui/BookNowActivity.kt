package com.prashant.smd.ui

import android.app.DatePickerDialog
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.prashant.smd.R
import com.prashant.smd.databinding.ActivityBookNowBinding
import com.prashant.smd.firebase.Constant
import com.prashant.smd.model.BookingModel
import com.prashant.smd.model.UserModel
import com.prashant.smd.util.CProgressDialog
import com.prashant.smd.util.LocalPreference
import com.prashant.stockmarketadviser.firebase.AuthManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookNowActivity : AppCompatActivity() {

    private lateinit var bind: ActivityBookNowBinding

    private var selectedOption: String? = null
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var user: UserModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityBookNowBinding.inflate(layoutInflater)
        setContentView(bind.root)

        user = AuthManager.userChecker(this) ?: UserModel()

        bind.backBtn.setOnClickListener {
            finish()
        }




        bind.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)

            // Change the text color of the selected RadioButton to black
            selectedRadioButton?.setTextColor(Color.BLACK)

            // Reset text color for all other RadioButtons
            for (i in 0 until bind.radioGroup.childCount) {
                val radioButton = bind.radioGroup.getChildAt(i) as? RadioButton
                if (radioButton != selectedRadioButton) {
                    radioButton?.setTextColor(resources.getColor(R.color.greyFont)) // Set the default color
                }
            }

            // Handle your other logic here
            selectedOption = selectedRadioButton?.text.toString()
            if (selectedOption == "Other") {
                bind.otherEventDetail.visibility = View.VISIBLE
            } else {
                bind.otherEventDetail.visibility = View.GONE
            }
        }


        bind.eventStartDate.setOnClickListener{
            showDatePickerDialog(bind.selectedStartDateView)
        }

        bind.eventEndDate.setOnClickListener{
            showDatePickerDialog(bind.selectedEndDateView)
        }

        bind.bookNowBtn.setOnClickListener{
            CProgressDialog.mShow(this@BookNowActivity)
            if (selectedOption == null || selectedOption!!.isEmpty()) {
                CProgressDialog.mDismiss()
                toast("Select any category!")
            }else if (selectedOption.equals("Other") && bind.otherEventDetail.text.toString().isEmpty()){
                CProgressDialog.mDismiss()
                toast("Please enter event name!")

            }else if (bind.selectedStartDateView.text.equals("Start Date")){
                CProgressDialog.mDismiss()
                toast("Select start date !")

            }else if (bind.selectedEndDateView.text.equals("End Date")){
                CProgressDialog.mDismiss()
                toast("Select End date !")

            }else if (bind.eventItemOverview.text.toString().isEmpty()){
                CProgressDialog.mDismiss()
                toast("Please write item overview !")

            }else{
                val model = BookingModel()
                model.name =user.name
                model.mobile =user.mobile
                if (selectedOption.equals("Other") ){
                    model.eventName=bind.otherEventDetail.text.toString()
                }else{
                    model.eventName= selectedOption.toString()
                }
                model.eventPlace =bind.eventPlace.text.toString()
                model.itemOverview =bind.eventItemOverview.text.toString()
                model.startDate = bind.selectedStartDateView.text.toString()
                model.endDate = bind.selectedEndDateView.text.toString()
                model.uid =Constant.bookingDb.push().key.toString()
                model.status ="pending"
                model.bookingDate =getCurrentDateTime()


                Constant.bookingDb.child(model.uid).setValue(model).addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        CProgressDialog.mDismiss()

                        toast("Dear ${model.name}, Thanks for booking we contact you soon !")
                        finish()
                    }
                }.addOnFailureListener {
                    CProgressDialog.mDismiss()
                }
            }
        }


        mediaPlayer = MediaPlayer.create(this, R.raw.booking_voice)

        bind.soundBtn.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }
    }

    private fun showDatePickerDialog(selectedDateView: TextView) {
        // Initialize a Calendar instance with the current date
        val calendar = Calendar.getInstance()

        // Set the minimum date to today's date to disable previous dates
        val datePickerDialog = DatePickerDialog(
            this@BookNowActivity,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val selectedDate = dateFormat.format(selectedCalendar.time)

                selectedDateView.text = selectedDate

                if (bind.selectedStartDateView.text.toString() != "Start Date") {
                    bind.selectedStartDateView.setTextColor(Color.BLACK)
                }

                if (bind.selectedEndDateView.text.toString() != "End Date") {
                    bind.selectedEndDateView.setTextColor(Color.BLACK)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // -1000 to exclude today
        datePickerDialog.show()
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.US)
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun toast(message: String?) {
        runOnUiThread {
            Toast.makeText(this@BookNowActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}