package com.bangkit.lokasee.ui.main.profile

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.store.UserStore.currentUser
import com.bangkit.lokasee.data.store.UserStore.currentUserToken
import com.bangkit.lokasee.databinding.FragmentProfileBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.ui.main.home.PostListAdapter
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible
import com.bangkit.lokasee.util.convertBitmapToFile
import com.bangkit.lokasee.util.createPartFromString
import com.bangkit.lokasee.util.getAvatarUrl
import com.bangkit.lokasee.util.uriToFile
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.transition.MaterialFadeThrough
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var currentAvatar: Bitmap? = null
    private var listPost = mutableListOf<Post>()
    private lateinit var profileViewModel: ProfileViewModel

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
        binding = FragmentProfileBinding.inflate(layoutInflater)
        setupViewModel()
        loadPost()
        Log.e("Ini addres",  currentUser.toString())
        binding.txtUserName.text = currentUser.name
        binding.txtUserEmail.text = currentUser.email
        binding.txtUserAddress.text = currentUser.address
        binding.txtUserPhone.text = currentUser.phoneNumber
        binding.inputUserAddress.editText?.setText(currentUser.address)
        binding.inputUserPhone.editText?.setText(currentUser.phoneNumber)

        binding.btnChangeProfile.setOnClickListener {
            binding.inputUserAddress.visible()
            binding.inputUserPhone.visible()
            binding.btnUpdateProfile.visible()
            binding.btnCancelProfile.visible()
            binding.btnChangeProfile.gone()
        }

        binding.btnCancelProfile.setOnClickListener {
            binding.inputUserAddress.gone()
            binding.inputUserPhone.gone()
            binding.btnUpdateProfile.gone()
            binding.btnCancelProfile.gone()
            binding.btnChangeProfile.visible()
            binding.inputUserAddress.editText?.setText(currentUser.address)
            binding.inputUserPhone.editText?.setText(currentUser.phoneNumber)
        }

        /*binding.btnUpdateProfile.setOnClickListener {
            when {
                binding.inputUserPhone.editText?.text.toString().isEmpty() -> {
                    with(binding) {
                        inputUserPhone.error = "Please insert your phone number"
                        inputUserPhone.requestFocus()
                    }
                }
                binding.inputUserAddress.editText?.text.toString().isEmpty() -> {
                    with(binding) {
                        inputUserAddress.error = "Please insert your address"
                        inputUserAddress.requestFocus()
                    }
                }
                else -> updateDataUser()
            }
        }

        binding.imgUserProfile.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .crop(2f, 2f)
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
        */

        Glide.with(requireActivity())
            .load(getAvatarUrl(currentUser))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(binding.imgUserProfile)

        if(currentUser.avatarUrl != ""){
            Glide.with(this)
                .asBitmap()
                .load(getAvatarUrl(currentUser))
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        currentAvatar = resource
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }

        return binding.root
    }

    private fun loadPost() {
        profileViewModel.getAuthUserPost().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progProfile.visible()
                        binding.txtErrorProfile.gone()
                        binding.btnReloadProfile.gone()
                    }

                    is Result.Success -> {
                        binding.progProfile.gone()
                        binding.txtErrorProfile.gone()
                        binding.btnReloadProfile.gone()
                        Log.e("Get User Posts", result.data.toString())
                        val resultResponse = result.data.data
                        if (resultResponse != null && resultResponse.isNotEmpty()){
                            listPost = resultResponse as MutableList<Post>
                            setPostData(listPost)
                        }
                    }

                    is Result.Error -> {
                        val error = if(result.error.contains("404")) "You don't have any post!" else "Something went wrong!"
                        binding.progProfile.gone()
                        binding.txtErrorProfile.visible()
                        binding.btnReloadProfile.visible()
                        binding.txtErrorProfile.text = error
                        binding.btnReloadProfile.setOnClickListener {
                            loadPost()
                        }
                    }
                }
            }
        }
    }

    fun updateAvatar(){
        val requestBody: HashMap<String, RequestBody> = HashMap()
        requestBody["name"] = createPartFromString(currentUser.name)
        requestBody["email"] = createPartFromString(currentUser.email)
        requestBody["phone_number"] = createPartFromString(binding.inputUserPhone.editText?.text.toString())
        requestBody["address"] = createPartFromString(binding.inputUserAddress.editText?.text.toString())
        val fileAvatar = convertBitmapToFile(currentAvatar!!, requireContext())
        val imagesBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), fileAvatar)
        val imagePart =  MultipartBody.Part.createFormData("image", fileAvatar.nameWithoutExtension, imagesBody);
        profileViewModel.updateAvatarUser(currentUser.id, requestBody, imagePart).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progProfileChange.visible()
                    }
                    is Result.Success -> {
                        val userData = result.data.data
                        if (userData != null){
                            binding.progProfileChange.gone()
                            currentUser.avatarUrl = userData.avatarUrl
                            profileViewModel.saveUser(
                                userData.id,
                                userData.name,
                                userData.email,
                                userData.phoneNumber,
                                userData.address,
                                userData.avatarUrl,
                                currentUserToken,
                            )
                            Toast.makeText(requireContext(), "Avatar successfully updated!", Toast.LENGTH_LONG).show()
                        }
                    }
                    is Result.Error -> {
                        binding.progProfileChange.gone()
                        Log.e("Error",  result.error)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun updateDataUser(){
        val requestBody: HashMap<String, RequestBody> = HashMap()
        requestBody["name"] = createPartFromString(currentUser.name)
        requestBody["email"] = createPartFromString(currentUser.email)
        requestBody["phone_number"] = createPartFromString(binding.inputUserPhone.editText?.text.toString())
        requestBody["address"] = createPartFromString(binding.inputUserAddress.editText?.text.toString())
        profileViewModel.updateDataUser(currentUser.id, requestBody).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progProfileChange.visible()
                        binding.btnCancelProfile.gone()
                        binding.btnUpdateProfile.gone()
                        binding.btnChangeProfile.gone()
                    }
                    is Result.Success -> {
                        val userData = result.data.data
                        if (userData != null){
                            binding.progProfileChange.gone()
                            binding.btnCancelProfile.gone()
                            binding.btnUpdateProfile.gone()
                            binding.inputUserAddress.gone()
                            binding.inputUserPhone.gone()
                            binding.btnChangeProfile.visible()
                            binding.txtUserAddress.text = userData.address
                            binding.txtUserPhone.text = userData.phoneNumber
                            binding.inputUserAddress.editText?.setText(userData.address)
                            binding.inputUserPhone.editText?.setText(userData.phoneNumber)
                            currentUser.phoneNumber = userData.phoneNumber
                            currentUser.address = userData.address
                            profileViewModel.saveUser(
                                userData.id,
                                userData.name,
                                userData.email,
                                userData.phoneNumber,
                                userData.address,
                                userData.avatarUrl,
                                currentUserToken,
                            )
                            Toast.makeText(requireContext(), "User successfully updated!", Toast.LENGTH_LONG).show()
                        }
                    }
                    is Result.Error -> {
                        binding.progProfileChange.gone()
                        binding.btnCancelProfile.visible()
                        binding.btnUpdateProfile.visible()
                        Log.e("Error",  result.error)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setPostData(listPost: List<Post>) {
        binding.rvUserPost.setHasFixedSize(true)
        binding.rvUserPost.layoutManager = LinearLayoutManager(requireContext())
        val listPostAdapter = PostListAdapter(listPost as MutableList<Post>)
        binding.rvUserPost.adapter = listPostAdapter
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        profileViewModel = factory.create(ProfileViewModel::class.java)
    }

    private val startForInputImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                val imageFile = uriToFile(fileUri, requireContext())
                val bitmap = BitmapFactory.decodeFile(imageFile.path)
                currentAvatar = bitmap
                Glide.with(requireActivity())
                    .load(bitmap)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(binding.imgUserProfile)
                updateAvatar()
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
        }
}