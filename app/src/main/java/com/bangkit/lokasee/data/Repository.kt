package com.bangkit.lokasee.data.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.lokasee.data.AppPreferences
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.body.BodyLogin
import com.bangkit.lokasee.data.body.BodyRegister
import com.bangkit.lokasee.data.response.*
import com.bangkit.lokasee.data.retrofit.ApiService
import com.bangkit.lokasee.data.store.FilterStore.currentFilter
import com.bangkit.lokasee.data.store.UserStore.currentUser
import com.bangkit.lokasee.data.store.UserStore.currentUserToken
import com.bangkit.lokasee.util.filterNotNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(private val apiService: ApiService, private val pref: AppPreferences) {
    // Auth Repo
    fun register(
        name: String,
        email: String,
        phoneNumber: String,
        address: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val bodyRegister = BodyRegister(name, email, phoneNumber, address, password)
            Log.e("Body Register", bodyRegister.toString())
            val response = apiService.register(bodyRegister)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
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
            Log.e("Ini mau logout", currentUserToken)
            val response = apiService.logout()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getUser(id: Int): LiveData<Result<UserUpdateResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getUser(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun updateAvatarUser(id: Int, params: Map<String, RequestBody>, image: MultipartBody.Part): LiveData<Result<UserUpdateResponse>> = liveData {
        emit(Result.Loading)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateUser( id, "PUT", params, image)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    }

    fun updateDataUser(id: Int, params: Map<String, RequestBody>): LiveData<Result<UserUpdateResponse>> = liveData {
        emit(Result.Loading)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateUserData( id, "PUT", params)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun saveAuthUser(
        userId: Int,
        userName: String,
        email: String,
        phoneNumber: String,
        address: String,
        avatarUrl: String,
        token: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            pref.saveUserLogin(userId, userName, email, phoneNumber,  address, avatarUrl, token)
            currentUser = pref.getUserLogin()
            currentUserToken = pref.getUserToken().first()
            Log.e("Ini habis login", currentUserToken)
        }
    }

    fun deleteAuthUser( ) {
        CoroutineScope(Dispatchers.IO).launch {
            pref.deleteUserLogin()
            currentUser = pref.getUserLogin()
            currentUserToken = pref.getUserToken().first()
            Log.e("Ini habis logout", currentUserToken)
        }
    }

    // Post Repo
    fun getAllPosts(): LiveData<Result<PostListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllPosts()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getAllPostsFiltered(): LiveData<Result<PostListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val filter = currentFilter.filterNotNull()
            val response = apiService.getAllPostsFiltered(null, filter)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun searchPostsFiltered(search: String, filter: Map<String, Int?>): LiveData<Result<PostListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val filterNotNull = filter.filterNotNull()
            val response = apiService.getAllPostsFiltered(search, filterNotNull)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getUserPosts(id: Int): LiveData<Result<PostListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getUserPosts(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getPost(id: Int): LiveData<Result<PostGetCreateUpdateDeleteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getPost(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun deletePost(id: Int): LiveData<Result<PostGetCreateUpdateDeleteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.deletePost(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun createPost(params: Map<String, RequestBody>, images: Array<MultipartBody.Part>): LiveData<Result<PostGetCreateUpdateDeleteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.testCreatePost(params, images)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun updatePost(id: Int, params: Map<String, RequestBody>, images: Array<MultipartBody.Part?>): LiveData<Result<PostGetCreateUpdateDeleteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.testUpdatePost( id, "PUT", params, images)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    // Location Repo
    fun getAllProvinsi(): LiveData<Result<ProvinsiListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllProvinsi()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getKabupatenByProvinsi(provinsiId : Int): LiveData<Result<KabupatenListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getKabupatenByProvinsi(provinsiId)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getKecamatanByKabupaten(kabupatenId : Int): LiveData<Result<KecamatanListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getKecamatanByKabupaten(kabupatenId)
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

        fun newInstance(
            apiService: ApiService,
            pref: AppPreferences
        ): Repository {
            instance = null
            return instance ?: synchronized(this) {
                instance ?: Repository(apiService, pref)
            }.also { instance = it }
        }
    }
}