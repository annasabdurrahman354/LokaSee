package com.bangkit.lokasee.ui.main.seller

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.*
import com.bangkit.lokasee.data.store.UserStore.currentUser
import com.bangkit.lokasee.databinding.FragmentSellerUpdateBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.ui.main.seller.adapter.InputPostImageListAdapter
import com.bangkit.lokasee.util.*
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.transition.MaterialFadeThrough
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.create
import okhttp3.RequestBody
import java.io.File


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SellerUpdateFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSellerUpdateBinding? = null
    private val binding get() = _binding!!
    private var selectedImages = mutableListOf<Bitmap>()
    private var provinsiList = mutableListOf<Provinsi>()
    private var kabupatenList = mutableListOf<Kabupaten>()
    private var kecamatanList = mutableListOf<Kecamatan>()
    private lateinit var selectedProvinsi: Provinsi
    private lateinit var selectedKabupaten: Kabupaten
    private lateinit var selectedKecamatan: Kecamatan
    private lateinit var sellerViewModel: SellerViewModel
    private lateinit var listPostImageListAdapter: InputPostImageListAdapter
    private lateinit var post: Post
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    private var latLong: LatLng? = null

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
        _binding = FragmentSellerUpdateBinding.inflate(inflater, container, false)
        setupViewModel()
        setupMapView()
        post = arguments?.getParcelable("POST")!!
        listPostImageListAdapter = InputPostImageListAdapter(selectedImages)
        binding.rvPostImageList.setHasFixedSize(true)
        binding.rvPostImageList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPostImageList.adapter = listPostImageListAdapter
        getProvinsiData()
        getKabupatenByProvinsi(post.provinsiId)
        getKecamatanByKabupaten(post.kabupatenId)
        getPostImages()
        latLong = LatLng(post.latitude, post.longitude)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            inputPostTitle.editText?.setText(post.title)
            inputPostDesc.editText?.setText(post.desc)
            inputPostPrice.editText?.setText(post.price.toString())
            inputPostArea.editText?.setText(post.area.toString())
            inputPostAddress.editText?.setText(post.address)
            inputPostAddress.editText?.setText(post.address)
            inputPostAddress.editText?.setText(post.address)
            selectPostProvinsi.editText?.setText(post.provinsi?.title)
            selectPostKabupaten.editText?.setText(post.kabupaten?.title)
            selectPostKecamatan.editText?.setText(post.kecamatan?.title)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPostUpdate.setOnClickListener{
            updatePost()
        }

        binding.btnPostDelete.setOnClickListener{
            deletePost()
        }

        binding.btnPostAddImage.setOnClickListener{
            ImagePicker.with(this)
                .compress(2048)
                .crop(4f, 3f)
                .galleryMimeTypes(
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .createIntent { intent ->
                    startForInputImageResult.launch(intent)
                }
        }
    }

    private fun setupMapView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        marker = mMap.addMarker(MarkerOptions().position(latLong!!).title("Post Position"))

        mMap.setOnMapLongClickListener {
            latLong = LatLng(it.latitude, it.longitude)
            marker!!.remove()
            marker = mMap.addMarker(MarkerOptions().position(latLong!!).title("New Post Position"))
        }
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

    private fun updatePost(){
        val postTitle = binding.inputPostTitle.editText?.text.toString()
        val postDesc = binding.inputPostDesc.editText?.text.toString()
        val postPrice = binding.inputPostPrice.editText?.text.toString()
        val postArea = binding.inputPostArea.editText?.text.toString()
        val postAddress = binding.inputPostAddress.editText?.text.toString()
        Log.e("Provinsi", provinsiList.toString())
        Log.e("Kabupaten", kabupatenList.toString())
        Log.e("Kecamatan", kecamatanList.toString())
        val postProvinsi = getProvinsi(
            binding.selectPostProvinsi.editText?.text.toString(), provinsiList
        )
        val postKabupaten = getKabupaten(
            binding.selectPostKabupaten.editText?.text.toString(), kabupatenList
        )
        val postKecamatan = getKecamatan(
            binding.selectPostKecamatan.editText?.text.toString(),
            kecamatanList
        )
        val postLatitude = latLong?.latitude
        val postLongitude = latLong!!.longitude

        when {
            postTitle.isEmpty() -> {
                with(binding) {
                    inputPostTitle.error = "Please input title first!"
                    inputPostTitle.requestFocus()
                }
            }
            postDesc.isEmpty() -> {
                with(binding) {
                    inputPostDesc.error = "Please input description first!"
                    inputPostDesc.requestFocus()
                }
            }
            postPrice.isEmpty() -> {
                with(binding) {
                    inputPostPrice.error = "Please input price first!"
                    inputPostPrice.requestFocus()
                }
            }
            postArea.isEmpty() -> {
                with(binding) {
                    inputPostArea.error = "Please input area wide first!"
                    inputPostArea.requestFocus()
                }
            }
            postAddress.isEmpty() -> {
                with(binding) {
                    inputPostAddress.error = "Please input address first!"
                    inputPostAddress.requestFocus()
                }
            }
            binding.selectPostProvinsi.editText?.text.toString().isEmpty() -> {
                with(binding) {
                    selectPostProvinsi.error = "Please select region first!"
                    selectPostProvinsi.requestFocus()
                }
            }
            binding.selectPostKabupaten.editText?.text.toString().isEmpty() -> {
                with(binding) {
                    selectPostKabupaten.error = "Please input city first!"
                    selectPostKabupaten.requestFocus()
                }
            }
            binding.selectPostKecamatan.editText?.text.toString().isEmpty() -> {
                with(binding) {
                    selectPostKecamatan.error = "Please input kecamatan first!"
                    selectPostKecamatan.requestFocus()
                }
            }
            postLatitude.toString().isEmpty()->{
                Toast.makeText(requireActivity(), "Please select location by long pressing in map!", Toast.LENGTH_LONG).show()
            }
            selectedImages.isEmpty() -> {
                Toast.makeText(requireActivity(), "Please select image first!", Toast.LENGTH_LONG).show()
            }
            else -> {
                val requestBody: HashMap<String, RequestBody> = HashMap()
                requestBody["title"] = createPartFromString(postTitle)
                requestBody["desc"] = createPartFromString(postDesc)
                requestBody["price"] = createPartFromString(postPrice)
                requestBody["area"] = createPartFromString(postArea)
                requestBody["address"] = createPartFromString(postAddress)
                requestBody["latitude"] = createPartFromString(postLatitude.toString())
                requestBody["longitude"] = createPartFromString(postLongitude.toString())
                requestBody["user_id"] = createPartFromString(currentUser.id.toString())
                requestBody["provinsi_id"] = createPartFromString(postProvinsi.id.toString())
                requestBody["kabupaten_id"] = createPartFromString(postKabupaten.id.toString())
                requestBody["kecamatan_id"] = createPartFromString(postKecamatan.id.toString())

                val postImagesParts: Array<MultipartBody.Part?> = arrayOfNulls(
                    selectedImages.size
                )

                val postImagesFile = mutableListOf<File>()

                for (index in 0 until selectedImages.size) {
                    postImagesFile.add(convertBitmapToFile(selectedImages[index], requireContext()))
                    val imagesBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), postImagesFile[index])
                    postImagesParts[index] =  MultipartBody.Part.createFormData("images[]", postImagesFile[index].nameWithoutExtension, imagesBody)
                }

                val pDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)

                pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                pDialog.titleText = "Updating Post"
                pDialog.setCancelable(false)
                pDialog.show()

                sellerViewModel.updatePost(post.id, requestBody, postImagesParts).observe(viewLifecycleOwner) {result ->
                    if (result != null) {
                        when (result) {
                            is Result.Success -> {
                                val provinsiData = result.data.data
                                if (provinsiData != null){
                                    pDialog.dismiss()
                                    findNavController().navigateUp()
                                    Toast.makeText(requireContext(), "Post successfully updated!", Toast.LENGTH_LONG).show()
                                }
                                pDialog.hide()
                            }
                            is Result.Error -> {
                                pDialog.hide()
                                Log.e("Error",  result.error)
                                Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deletePost(){
        val pDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Are You Sure?"
        pDialog.confirmText = "Delete"
        pDialog.setCancelable(true)
        pDialog.setConfirmClickListener {
            pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE)
            sellerViewModel.deletePost(post.id).observe(viewLifecycleOwner) {result ->
                if (result != null) {
                    when (result) {
                        is Result.Success -> {
                            val provinsiData = result.data.data
                            if (provinsiData != null){
                                pDialog.dismiss()
                                findNavController().navigateUp()
                                Toast.makeText(requireContext(), "Post successfully deleted!", Toast.LENGTH_LONG).show()
                            }
                            pDialog.hide()
                        }
                        is Result.Error -> {
                            pDialog.hide()
                            Log.e("Error",  result.error)
                            Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
        pDialog.show()
    }

    private fun getPostImages() {
        post.images.forEach { url ->
            Glide.with(this)
                .asBitmap()
                .load(getStorageUrl(url))
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        selectedImages.add(resource)
                        listPostImageListAdapter.notifyDataSetChanged()
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }
    }

    private fun getProvinsiData() {
        sellerViewModel.getAllProvinsi().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progLocation.visible()
                        binding.selectPostProvinsi.gone()
                        binding.selectPostKabupaten.gone()
                        binding.selectPostKecamatan.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Success -> {
                        val provinsiData = result.data.data
                        if (provinsiData?.isNotEmpty() == true){
                            provinsiList = provinsiData as MutableList<Provinsi>
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, provinsiList)
                        binding.atvProvinsi.setAdapter(arrayAdapter)
                        binding.selectPostProvinsi.editText?.addTextChangedListener {
                            if(it.toString() != ""){
                                selectedProvinsi = getProvinsi(
                                    binding.selectPostProvinsi.editText?.text.toString(),
                                    provinsiList
                                )
                                binding.selectPostKabupaten.editText?.text?.clear()
                                binding.selectPostKecamatan.editText?.text?.clear()
                                getKabupatenByProvinsi(selectedProvinsi.id)
                            }
                        }

                        binding.progLocation.gone()
                        binding.btnReload.gone()
                        binding.selectPostProvinsi.visible()
                    }
                    is Result.Error -> {
                        Log.e("Error",  result.error)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectPostProvinsi.gone()
                        binding.btnReload.visible()
                        binding.btnReload.setOnClickListener {
                            getProvinsiData()
                        }
                    }
                }
            }
        }
    }

    private fun getKabupatenByProvinsi(provinsiId: Int) {
        sellerViewModel.getKabupatenByProvinsi(provinsiId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progLocation.visible()
                        binding.selectPostKabupaten.gone()
                        binding.selectPostKecamatan.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Success -> {
                        val kabupatenData = result.data.data
                        if (kabupatenData?.isNotEmpty() == true){
                            kabupatenList = kabupatenData as MutableList<Kabupaten>
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, kabupatenList)
                        binding.atvKabupaten.setAdapter(arrayAdapter)
                        binding.selectPostKabupaten.editText?.addTextChangedListener {
                            if(it.toString() != ""){
                                selectedKabupaten = getKabupaten(
                                    binding.selectPostKabupaten.editText?.text.toString(),
                                    kabupatenList
                                )
                                binding.selectPostKecamatan.editText?.text?.clear()
                                getKecamatanByKabupaten(selectedKabupaten.id)
                            }
                        }
                        binding.selectPostKabupaten.visible()
                        binding.progLocation.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Error -> {
                        Log.e("Error",  result.error)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectPostKabupaten.gone()
                        binding.btnReload.visible()
                        binding.btnReload.setOnClickListener {
                            getKabupatenByProvinsi(provinsiId)
                        }
                    }
                }
            }
        }
    }

    private fun getKecamatanByKabupaten(kabupatenId: Int) {
        sellerViewModel.getKecamatanByKabupaten(kabupatenId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.selectPostKecamatan.gone()
                        binding.progLocation.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Success -> {
                        val kecamatanData = result.data.data
                        if (kecamatanData?.isNotEmpty() == true){
                            kecamatanList = kecamatanData as MutableList<Kecamatan>
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, kecamatanList)
                        binding.atvKecamatan.setAdapter(arrayAdapter)
                        binding.selectPostKecamatan.editText?.addTextChangedListener {
                            if(it.toString() != "") {
                                selectedKecamatan = getKecamatan(
                                    binding.selectPostKecamatan.editText?.text.toString(),
                                    kecamatanList
                                )
                            }
                        }
                        binding.selectPostKecamatan.visible()
                        binding.progLocation.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Error -> {
                        Log.e("Error",  result.error)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectPostKecamatan.gone()
                        binding.btnReload.visible()
                        binding.btnReload.setOnClickListener {
                            getKecamatanByKabupaten(kabupatenId)
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModel(){
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        sellerViewModel = factory.create(SellerViewModel::class.java)
    }

    private val startForInputImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                val imageFile = uriToFile(fileUri, requireContext())
                val bitmap = BitmapFactory.decodeFile(imageFile.path)
                selectedImages.add(bitmap)
                listPostImageListAdapter.notifyDataSetChanged()
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}