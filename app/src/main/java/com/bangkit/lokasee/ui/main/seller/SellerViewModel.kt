package com.bangkit.lokasee.ui.main.seller

import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.repo.Repository
import com.bangkit.lokasee.data.store.UserStore.currentUser
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SellerViewModel(private val repository: Repository) : ViewModel() {
    // POST
    fun getPost(id: Int) = repository.getPost(id)
    fun getAuthUserPost() = repository.getUserPosts(currentUser.id)
    fun createPost(params: Map<String, RequestBody>, images: Array<MultipartBody.Part>) = repository.createPost(params, images)
    fun updatePost(id: Int, params: Map<String, RequestBody>, images: Array<MultipartBody.Part?>) = repository.updatePost(id, params, images)
    fun deletePost(id: Int) = repository.deletePost(id)

    //LOCATION
    fun getAllProvinsi() = repository.getAllProvinsi()
    fun getKabupatenByProvinsi(provinsiId: Int) = repository.getKabupatenByProvinsi(provinsiId)
    fun getKecamatanByKabupaten(kabupatenId: Int) = repository.getKecamatanByKabupaten(kabupatenId)
}