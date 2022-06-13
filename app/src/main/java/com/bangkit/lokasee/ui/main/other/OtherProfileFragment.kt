package com.bangkit.lokasee.ui.main.other

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.store.UserStore.currentUser
import com.bangkit.lokasee.databinding.FragmentOtherProfileBinding
import com.bangkit.lokasee.util.getAvatarUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.transition.MaterialFadeThrough

class OtherProfileFragment : Fragment() {
    private lateinit var binding: FragmentOtherProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherProfileBinding.inflate(layoutInflater)

        binding.txtOtherName.text = currentUser.name
        binding.txtOtherEmail.text = currentUser.email
        Glide.with(requireActivity())
            .load(getAvatarUrl(currentUser))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(binding.imgOtherProfile)
        return binding.root
    }
}