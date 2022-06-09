package com.bangkit.lokasee.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.lokasee.data.body.BodyLogin
import com.bangkit.lokasee.data.body.BodyRegister
import com.bangkit.lokasee.data.response.LoginResponse
import com.bangkit.lokasee.data.response.LogoutResponse
import com.bangkit.lokasee.data.response.RegisterResponse
import com.bangkit.lokasee.util.AppPreferences
import com.bangkit.lokasee.util.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Repository(private val apiService: ApiService, private val pref: AppPreferences) {
    fun register(
        name: String,
        email: String,
        phoneNumber: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(BodyRegister(name, email, phoneNumber, password))
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun saveUser(
        userId: Int,
        userName: String,
        email: String,
        phoneNumber: String,
        avatarUrl: String,
        token: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            pref.saveUserLogin(userId, userName, email, phoneNumber, avatarUrl, token)
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(BodyLogin(email, password))
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun logout(): LiveData<Result<LogoutResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.logout()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

//    fun getAllMarkerMaps(token: String, location: Int): LiveData<Result<StoryResponse>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.getAllMarkerMaps(token, location)
//            emit(Result.Success(response))
//        } catch (e: Exception) {
//            Log.e(TAG, "onFailure: ${e.message.toString()}")
//            emit(Result.Error(e.message.toString()))
//        }
//    }
//
//    fun uploadStory(
//        token: String,
//        file: MultipartBody.Part,
//        description: RequestBody,
//        lat: RequestBody,
//        lon: RequestBody
//    ): LiveData<Result<FileUploadResponse>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.uploadImage(token, file, description, lat, lon)
//            emit(Result.Success(response))
//        } catch (e: Exception) {
//            Log.e(TAG, "onFailure: ${e.message.toString()}")
//            emit(Result.Error(e.message.toString()))
//        }
//    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            pref: AppPreferences
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, pref)
            }.also { instance = it }
    }
}