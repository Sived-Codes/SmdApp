package com.prashant.smd.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nguyenhoanglam.imagepicker.model.Image
import com.prashant.smd.R
import com.squareup.picasso.Picasso

internal class ImageAdapter(private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val images = ArrayList<Image>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(inflater.inflate(R.layout.cl_selected_image_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        if (position in 0 until images.size) {
            val (uri) = images[position]

            holder.remove.setOnClickListener {
                try {
                    images.removeAt(position)
                    notifyItemRangeRemoved(position, 1) // Remove one item
                } catch (e: Exception) {
                    Log.d("TAG", "onBindViewHolder: $e")
                }
            }

            Picasso.get().load(uri).into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setData(images: List<Image>?) {
        this.images.clear()
        if (images != null) {
            this.images.addAll(images)
        }
        notifyDataSetChanged()
    }

    internal class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.selected_image_view)
        var remove: TextView = itemView.findViewById(R.id.selected_image_remove)
    }
}
