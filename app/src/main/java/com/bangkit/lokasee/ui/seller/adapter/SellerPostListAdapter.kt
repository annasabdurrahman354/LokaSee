package com.bangkit.lokasee.ui.seller.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.databinding.ItemPostBinding
import com.bangkit.lokasee.util.retrofit.ApiConfig
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.lang.StringBuilder

class SellerPostListAdapter(private var data:MutableList<Post>): RecyclerView.Adapter<SellerPostListAdapter.ListViewHolder>() {
    class ListViewHolder(var binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val imageUrlList = mutableListOf<CarouselItem>()
        data[position].images.forEach {
            imageUrlList.add(CarouselItem(ApiConfig.HOST + it))
        }
        holder.binding.carouselPost.setData(imageUrlList)
        holder.binding.cardPost.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("POST", data[position])
            it.findNavController().navigate(R.id.action_sellerHomeFragment_to_sellerUpdateFragment, bundle)
        }
    }
    override fun getItemCount(): Int = data.size
}
