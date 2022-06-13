package com.bangkit.lokasee.ui.main.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.repo.Repository
import com.bangkit.lokasee.data.response.PostListResponse

class OtherViewModel(private val repository: Repository) : ViewModel() {
    private val _listPost =  MutableLiveData<Result<PostListResponse>>()
    val listPost: LiveData<Result<PostListResponse>> = _listPost

    fun getUser(id: Int) = repository.getUser(id)
    fun getUserPosts(id: Int) = repository.getUserPosts(id)
}