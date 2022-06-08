package com.bangkit.lokasee.ui.main.home

import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.Repository

class HomeViewModel(private val repository: Repository) : ViewModel() {

    fun getAllPosts() = repository.getAllPosts()

    fun logout() = repository.logout()

}