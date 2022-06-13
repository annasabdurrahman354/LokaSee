package com.bangkit.lokasee.ui.main.seller

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.databinding.FragmentSellerHomeBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.ui.main.seller.adapter.SellerPostListAdapter
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible
import com.google.android.material.transition.MaterialFadeThrough

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SellerHomeFragment : Fragment() {

    private var _binding: FragmentSellerHomeBinding? = null
    private val binding get() = _binding!!
    private var listPost = mutableListOf<Post>()
    private lateinit var sellerViewModel: SellerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupViewModel()
        _binding = FragmentSellerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPost()
    }

    private fun loadPost() {
        sellerViewModel.getAuthUserPost().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progSellerHome.visible()
                        binding.txtErrorSellerHome.gone()
                        binding.btnReloadSellerHome.gone()
                    }

                    is Result.Success -> {
                        binding.progSellerHome.gone()
                        binding.txtErrorSellerHome.gone()
                        binding.btnReloadSellerHome.gone()
                        Log.e("Get User Posts", result.data.toString())
                        val resultResponse = result.data.data
                        if (resultResponse != null){
                            listPost = resultResponse as MutableList<Post>
                            setPostData(listPost)
                        }
                    }

                    is Result.Error -> {
                        val error = if(result.error.contains("404")) "You don't have any post!" else "Something went wrong!"
                        binding.progSellerHome.gone()
                        binding.txtErrorSellerHome.visible()
                        binding.btnReloadSellerHome.visible()
                        binding.txtErrorSellerHome.text = error
                        binding.btnReloadSellerHome.setOnClickListener {
                            loadPost()
                        }
                    }
                }
            }
        }
    }

    private fun setPostData(listPost: List<Post>) {
        binding.rvSellerPost.setHasFixedSize(true)
        binding.rvSellerPost.layoutManager = LinearLayoutManager(requireContext())
        val listPostAdapter = SellerPostListAdapter(listPost as MutableList<Post>)
        binding.rvSellerPost.adapter = listPostAdapter
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        sellerViewModel = factory.create(SellerViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}