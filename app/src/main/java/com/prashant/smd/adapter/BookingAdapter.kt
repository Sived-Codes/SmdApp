package com.prashant.smd.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.prashant.smd.R
import com.prashant.smd.databinding.ClAlertBinding
import com.prashant.smd.firebase.Constant
import com.prashant.smd.model.BookingModel
import com.prashant.smd.model.ImageHomeModel
import com.prashant.smd.util.CProgressDialog


class BookingAdapter(
    options: FirebaseRecyclerOptions<BookingModel>, private val context: Context
) : FirebaseRecyclerAdapter<BookingModel, BookingAdapter.ViewHolder>(options) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.event_title)
        val bookingDate: TextView = itemView.findViewById(R.id.event_booking_date)
        val eventDate: TextView = itemView.findViewById(R.id.event_date)
        val eventOrderStatus: TextView = itemView.findViewById(R.id.eventOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.cl_booking_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BookingModel) {
        // Your code to bind data to views
        holder.eventName.text = model.eventName
        holder.eventDate.text = "Event : ${model.startDate} - ${model.endDate}"
        holder.eventOrderStatus.text = model.status
        holder.bookingDate.text =model.bookingDate

        holder.itemView.setOnClickListener{
            showDeleteConfirmationDialog(model)

        }
    }

    private fun showDeleteConfirmationDialog(model: BookingModel) {
        val dialogBinding = ClAlertBinding.inflate(LayoutInflater.from(context))
        val dialog = androidx.appcompat.app.AlertDialog.Builder(context, R.style.MaterialAlertDialog_Rounded)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.alertText.text = "Are you sure you want to delete this booking ?"
        dialogBinding.yesBtn.setOnClickListener {
            dialog.dismiss()
            CProgressDialog.mShow(context)

            Constant.bookingDb.child(model.uid).removeValue()
                .addOnCompleteListener {
                    CProgressDialog.mDismiss()
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    CProgressDialog.mDismiss()
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }

        }

        dialogBinding.noBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
