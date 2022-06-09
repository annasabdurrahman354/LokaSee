package com.bangkit.lokasee.ui.auth.register

import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    fun register(name: String, email: String, phoneNumber: String, password: String) =
        repository.register(name, email, phoneNumber, password)
}