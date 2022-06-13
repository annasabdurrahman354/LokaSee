package com.bangkit.lokasee.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.store.FilterStore
import com.bangkit.lokasee.databinding.FragmentSearchBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.ui.main.FilterBottomSheet
import com.bangkit.lokasee.ui.main.home.PostListAdapter
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible
import com.bangkit.lokasee.util.isMapEmpty
import com.google.android.material.transition.MaterialFadeThrough

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private var listPost = mutableListOf<Post>()

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
        binding = FragmentSearchBinding.inflate(layoutInflater)
        setupViewModel()



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnFilter.setOnClickListener {
            getFragmentManager()?.let { FilterBottomSheet().show(it, FilterBottomSheet.TAG) }
        }
        binding.inputSearch.addTextChangedListener{
            loadPost(it.toString())
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun loadPost(search: String) {
        searchViewModel.searchPostsFiltered(search).observe(viewLifecycleOwner){ result->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progSearch.visible()
                        binding.txtErrorSearch.gone()
                        binding.btnReloadSearch.gone()
                    }

                    is Result.Success -> {
                        binding.progSearch.gone()
                        binding.txtErrorSearch.gone()
                        binding.btnReloadSearch.gone()
                        val resultResponse = result.data.data
                        if (resultResponse != null){
                            listPost = resultResponse as MutableList<Post>
                            if(FilterStore.currentFilter.isMapEmpty() && listPost.isEmpty()){
                                val empty = "Nothing match with filter!"
                                binding.btnReloadSearch.visible()
                                binding.txtErrorSearch.visible()
                                binding.txtErrorSearch.text = empty
                                binding.btnReloadSearch.setOnClickListener {
                                    loadPost(search)
                                }
                            }
                            else if(!FilterStore.currentFilter.isMapEmpty() && listPost.isEmpty()){
                                val empty = "Nothing match with filter!"
                                binding.btnReloadSearch.visible()
                                binding.txtErrorSearch.visible()
                                binding.txtErrorSearch.text = empty
                                binding.btnReloadSearch.setOnClickListener {
                                    loadPost(search)
                                }
                            }
                            else setPostData(listPost)
                        }
                    }

                    is Result.Error -> {
                        val error = "Something went wrong!"
                        binding.progSearch.gone()
                        binding.txtErrorSearch.visible()
                        binding.txtErrorSearch.text = result.error
                        binding.btnReloadSearch.setOnClickListener {
                            loadPost(search)
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModel(){
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        searchViewModel = factory.create(SearchViewModel::class.java)
    }

    private fun setPostData(listPost: List<Post>) {
        binding.rvSearch.setHasFixedSize(true)
        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
        val listPostAdapter = PostListAdapter(listPost as MutableList<Post>)
        binding.rvSearch.adapter = listPostAdapter
    }
}
