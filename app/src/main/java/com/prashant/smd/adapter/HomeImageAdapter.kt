package com.prashant.smd.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.prashant.smd.R
import com.prashant.smd.databinding.ClAlertBinding
import com.prashant.smd.firebase.Constant
import com.prashant.smd.model.ImageHomeModel
import com.prashant.smd.util.CProgressDialog
import com.squareup.picasso.Picasso

class HomeImageAdapter(
    options: FirebaseRecyclerOptions<ImageHomeModel>,
    private val context: Context
) : FirebaseRecyclerAdapter<ImageHomeModel, HomeImageAdapter.ViewHolder>(options) {

    fun clear() {
        snapshots.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.homeImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cl_home_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ImageHomeModel) {
        // Your code to bind data to views
        holder.imageView.setOnClickListener {
            showDeleteConfirmationDialog(model)
        }

        Picasso.get().load(model.imageUrl).placeholder(R.drawable.image_place_holder).into(holder.imageView)
    }

    private fun showDeleteConfirmationDialog(model: ImageHomeModel) {
        val dialogBinding = ClAlertBinding.inflate(LayoutInflater.from(context))
        val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.alertText.text = "Are you sure you want to delete this photo?"
        dialogBinding.yesBtn.setOnClickListener {
            dialog.dismiss()
            deleteImageAndData(model)
        }

        dialogBinding.noBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteImageAndData(model: ImageHomeModel) {
        CProgressDialog.mShow(context)

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${model.imageName}")

        imageRef.delete()
            .addOnSuccessListener {
                Constant.homeDB.child(model.uid).removeValue()
                    .addOnCompleteListener {
                        CProgressDialog.mDismiss()
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        CProgressDialog.mDismiss()
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                CProgressDialog.mDismiss()
                if (e is com.google.firebase.storage.StorageException &&
                    e.errorCode == com.google.firebase.storage.StorageException.ERROR_OBJECT_NOT_FOUND
                ) {
                    Log.d("FB", "Object not found: $e")
                } else {
                    Log.d("FB", "Error deleting image: $e")
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
