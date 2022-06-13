package com.bangkit.lokasee.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.databinding.ItemPostBinding
import com.bangkit.lokasee.data.retrofit.ApiConfig
import com.bangkit.lokasee.util.getAvatarUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class PostListAdapter(private var data:MutableList<Post>): RecyclerView.Adapter<PostListAdapter.ListViewHolder>() {
    class ListViewHolder(var binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val post = data[position]
        val imageUrlList = mutableListOf<CarouselItem>()
        post.images.forEach {
            imageUrlList.add(CarouselItem(ApiConfig.HOST + it))
        }
        with(holder.binding){
            carouselPost.setData(imageUrlList)
            txtPostTitle.text = post.title
            txtPostPrice.text = "Rp ${post.price.toString()}"
            txtPostArea.text = "${post.area} m²"
            txtPostLocation.text = "${post.kecamatan!!.title}, ${post.kabupaten!!.title}"
            Glide.with(root)
                .load(getAvatarUrl(post.user!!))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imgPostSellerProfile)
            root.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("POST", post)
                it.findNavController().navigate(R.id.action_global_postFragment, bundle)
            }
        }
    }
    override fun getItemCount(): Int = data.size
}
