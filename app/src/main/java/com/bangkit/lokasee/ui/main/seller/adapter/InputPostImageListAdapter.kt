package com.bangkit.lokasee.ui.main.seller.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.lokasee.databinding.ItemInputPostImageBinding
import java.io.File

class InputPostImageListAdapter(private var data : MutableList<Bitmap>): RecyclerView.Adapter<InputPostImageListAdapter.ListViewHolder>() {
    class ListViewHolder(var binding: ItemInputPostImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemInputPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.binding.imgPostImageList.setImageBitmap(data[position])
        holder.binding.btnDeletePostImageList.setOnClickListener {
            data.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, data.size)
        }
    }
    override fun getItemCount(): Int = data.size
}
