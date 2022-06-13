package com.bangkit.lokasee.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.repo.Repository
import com.bangkit.lokasee.data.response.PostListResponse

class HomeViewModel(private val repository: Repository) : ViewModel() {
    private val _listPost =  MutableLiveData<Result<PostListResponse>>()
    val listPost: LiveData<Result<PostListResponse>> = _listPost

    // POST
    fun getAllPostsFiltered() = repository.getAllPostsFiltered()

    //LOCATION
    fun getAllProvinsi() = repository.getAllProvinsi()
    fun getKabupatenByProvinsi(provinsiId: Int) = repository.getKabupatenByProvinsi(provinsiId)
    fun getKecamatanByKabupaten(kabupatenId: Int) = repository.getKecamatanByKabupaten(kabupatenId)
}