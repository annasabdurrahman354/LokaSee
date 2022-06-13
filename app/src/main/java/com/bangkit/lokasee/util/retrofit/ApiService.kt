package com.bangkit.lokasee.util.retrofit

import com.bangkit.lokasee.data.body.BodyLogin
import com.bangkit.lokasee.data.body.BodyRegister
import com.bangkit.lokasee.data.response.LoginResponse
import com.bangkit.lokasee.data.response.LogoutResponse
import com.bangkit.lokasee.data.response.PostListResponse
import com.bangkit.lokasee.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("post")
    suspend fun uploadPost(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
    ): PostListResponse

    @POST("login")
    suspend fun login(@Body params: BodyLogin): LoginResponse

    @POST("register")
    suspend fun register(@Body params: BodyRegister): RegisterResponse

    @POST("logout")
    suspend fun logout(): LogoutResponse
}