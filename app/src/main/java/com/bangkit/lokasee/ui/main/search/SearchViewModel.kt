package com.bangkit.lokasee.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.repo.Repository
import com.bangkit.lokasee.data.response.PostListResponse
import com.bangkit.lokasee.data.store.FilterStore.currentFilter
import com.bangkit.lokasee.data.store.KABUPATEN
import com.bangkit.lokasee.data.store.KECAMATAN
import com.bangkit.lokasee.data.store.PROVINSI

class SearchViewModel(private val repository: Repository) : ViewModel() {
    private var _listPost =  MutableLiveData<Result<PostListResponse>>()
    val listPost: LiveData<Result<PostListResponse>> = _listPost

    // POST
    fun searchPostsFiltered(search: String) :  LiveData<com.bangkit.lokasee.data.Result<PostListResponse>> {
        val filter = currentFilter
        filter[PROVINSI] = null
        filter[KABUPATEN] = null
        filter[KECAMATAN] = null
        return repository.searchPostsFiltered(search, filter)
    }
}