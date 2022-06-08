package com.bangkit.lokasee.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.databinding.FragmentHomeBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.btnLogout.setOnClickListener {
            homeViewModel.logout().observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visible()
                        }

                        is Result.Success -> {
                            binding.progressBar.gone()
                            Snackbar.make(binding.root, result.data.message, Snackbar.LENGTH_LONG)
                                .show()
                            activity?.finish()
                        }

                        is Result.Error -> {
                            binding.progressBar.gone()
                            Snackbar.make(binding.root, result.error, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        homeViewModel = factory.create(HomeViewModel::class.java)
    }

    private fun getAAllPost(){
        homeViewModel.getAllPosts().observe(viewLifecycleOwner){ result->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visible()
                    }

                    is Result.Success -> {
                        binding.progressBar.gone()
                        // TODO STORE DATA TO ADAPTER
                    }

                    is Result.Error -> {
                        binding.progressBar.gone()
                        // TODO GIVE SOME ERROR
                        Snackbar.make(binding.root, result.error, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


}
