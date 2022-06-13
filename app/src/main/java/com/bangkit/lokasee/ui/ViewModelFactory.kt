package com.bangkit.lokasee.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.lokasee.data.Repository
import com.bangkit.lokasee.di.Injection
import com.bangkit.lokasee.ui.auth.login.LoginViewModel
import com.bangkit.lokasee.ui.auth.register.RegisterViewModel
import com.bangkit.lokasee.ui.main.MainViewModel
import com.bangkit.lokasee.ui.main.home.HomeViewModel
import com.bangkit.lokasee.ui.main.map.MapViewModel
import com.bangkit.lokasee.ui.main.profile.ProfileViewModel
import com.bangkit.lokasee.ui.main.seller.SellerViewModel

class ViewModelFactory(private val repository: Repository) :  ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
               HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SellerViewModel::class.java) -> {
                SellerViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile

        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance
            ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }

        fun newInstance(context: Context): ViewModelFactory{
            instance = null
            return  instance ?: synchronized(this) {
                    instance ?: ViewModelFactory(Injection.reProvideRepository(context))
            }.also { instance = it }
        }
    }
}