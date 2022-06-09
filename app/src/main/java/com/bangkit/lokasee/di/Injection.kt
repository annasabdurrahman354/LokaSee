package com.bangkit.lokasee.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bangkit.lokasee.data.Repository
import com.bangkit.lokasee.util.AppPreferences
import com.bangkit.lokasee.util.retrofit.ApiConfig

object Injection {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app")

    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val userPreference = AppPreferences.getInstance(context.dataStore)
        return Repository.getInstance(apiService, userPreference)
    }
}