package com.bangkit.lokasee.ui.main.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.store.FilterStore.currentFilter
import com.bangkit.lokasee.databinding.FragmentMapBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.util.isMapEmpty
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupMapView()
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(requireContext())
        mapViewModel = factory.create(MapViewModel::class.java)
    }

    private fun setupMapView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun loadPost() {
        val defaultCameraSetup = LatLng(-6.200000, 106.816666)
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                defaultCameraSetup,
                5f
            )
        )
        mapViewModel.getAllPostsFiltered().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Success -> {
                        val resultResponse = result.data.data
                        val mapMarker = mutableMapOf<Int, Any>()
                        if (resultResponse != null) {
                            if(currentFilter.isMapEmpty() && resultResponse.isEmpty()){
                                Toast.makeText(
                                    requireContext(),
                                    "There is no post data yet!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            else if(!currentFilter.isMapEmpty() && resultResponse.isEmpty()){
                                Toast.makeText(
                                    requireContext(),
                                    "Nothing match with filter!",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                            else {
                                resultResponse.forEach {

                                    if (it != null) {
                                        val latLng = LatLng(it.latitude, it.longitude)
                                        mMap.addMarker(
                                            MarkerOptions()
                                                .position(latLng)
                                                .title(it.title)
                                        )?.let { it1 -> mapMarker.put(it.id, it1)  }
                                    }
                                }

                                mMap.setOnInfoWindowClickListener {
                                    for (i in mapMarker){
                                        if (i.value == it){
                                            for (j in resultResponse){
                                                if (j != null) {
                                                    if (j.id == i.key){
                                                        val bundle = Bundle()
                                                        bundle.putParcelable("POST", j)
                                                        findNavController().navigate(R.id.action_mapFragment_to_postFragment, bundle)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.message_alert_register_failed),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
        googleMap.setPadding(0,40,0, 160)
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        loadPost()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
            else{
                Toast.makeText(
                    requireContext(),
                    "You need grant permission to access location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}