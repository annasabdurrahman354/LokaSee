package com.bangkit.lokasee.ui.auth.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.lokasee.R
import com.bangkit.lokasee.databinding.FragmentOnboardingBinding
import org.imaginativeworld.whynotimagecarousel.listener.CarouselOnScrollListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem


class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCarousel()
        setupAction()
    }

    private fun setupCarousel() {
        val list: MutableList<CarouselItem> = ArrayList()

        list.add(
            CarouselItem(
                "https://images.unsplash.com/photo-1532581291347-9c39cf10a73c?w=1080",
            )
        )

        list.add(
            CarouselItem(
                "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080"
            )
        )

        binding.carouselOnboarding.apply {
            registerLifecycle(lifecycle)
            setData(list)
            onScrollListener = object : CarouselOnScrollListener {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int,
                    position: Int,
                    carouselItem: CarouselItem?
                ) {
                    // ...
                }

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                    position: Int,
                    carouselItem: CarouselItem?
                ) {
                    when (position){
                        0 -> {
                            binding.apply {
                                tvCarouselTitle.text = "Find the Perfect Place"
                                tvCarouselSubtitle.text = "Find perfect place for your next business with our recommendation system."
                            }
                        }

                        1 -> {
                            binding.apply {
                                tvCarouselTitle.text = "ITEM 1"
                                tvCarouselSubtitle.text = "Ini adalah item 1"
                            }
                        }
                    }



                }
            }
        }
    }

    private fun setupAction(){
        binding.apply {
            btnGoToLogin.setOnClickListener {
                findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
            }

            btnSkip.setOnClickListener {
                //TODO GO WITHOUT AUTH
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}