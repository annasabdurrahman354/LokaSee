package com.bangkit.lokasee.ui.main.map

import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.repo.Repository

class MapViewModel(private val repository: Repository) : ViewModel() {
    fun getAllPostsFiltered() = repository.getAllPostsFiltered()
}