package com.bangkit.lokasee.ui.main.home

import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.Repository

class HomeViewModel(private val repository: Repository) : ViewModel() {

    fun logout() = repository.logout()
}