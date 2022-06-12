package com.bangkit.lokasee.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.data.repo.Repository
import com.bangkit.lokasee.data.response.PostListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileViewModel(private val repository: Repository) : ViewModel() {
    private val _listPost =  MutableLiveData<Result<PostListResponse>>()
    val listPost: LiveData<Result<PostListResponse>> = _listPost

    fun updateUser(id: Int, params: Map<String, RequestBody>, image: MultipartBody.Part) = repository.updateUser(id, params, image)
    fun getUser(id: Int) = repository.getUser(id)
    fun getUserPosts(id: Int) = repository.getUserPosts(id)
}