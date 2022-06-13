package com.bangkit.lokasee.data.retrofit

import com.bangkit.lokasee.data.body.BodyLogin
import com.bangkit.lokasee.data.body.BodyRegister
import com.bangkit.lokasee.data.response.*
import com.bangkit.lokasee.data.store.UserStore.currentUserToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("post")
    suspend fun getAllPosts(): PostListResponse

    @GET("post/filter")
    suspend fun getAllPostsFiltered(
        @Query("search") search: String?,
        @QueryMap options: Map<String, Int?>
    ): PostListResponse

    @GET("post/{id}")
    suspend fun getPost(
        @Path("id") id: Int,
    ): PostGetCreateUpdateDeleteResponse

    @GET("post/user/{id}")
    suspend fun getUserPosts(@Path("id") id: Int): PostListResponse

    @DELETE("post/{id}")
    suspend fun deletePost(
        @Path("id") id: Int,
    ): PostGetCreateUpdateDeleteResponse

    @Multipart
    @POST("post")
    suspend fun testCreatePost(
        @PartMap params: @JvmSuppressWildcards Map<String,RequestBody>,
        @Part images: @JvmSuppressWildcards Array<MultipartBody.Part>,
    ): PostGetCreateUpdateDeleteResponse

    @Multipart
    @POST("post/{id}")
    suspend fun testUpdatePost(
        @Path("id") id: Int,
        @Query("_method") _method: String = "PUT",
        @PartMap params: @JvmSuppressWildcards Map<String,RequestBody>,
        @Part images: @JvmSuppressWildcards Array<MultipartBody.Part?>,
    ): PostGetCreateUpdateDeleteResponse

    /* @Multipart
    @POST("post")
    suspend fun createPost(
        @Part("title") title: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part("price") price: RequestBody,
        @Part("area") area: RequestBody,
        @Part("address") address: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("provinsi_id") provinsi_id: RequestBody,
        @Part("kabupaten_id") kabupaten_id: RequestBody,
        @Part("kecamatan_id") kecamatan_id: RequestBody,
        @Part images: Array<MultipartBody.Part>,
    ): PostGetCreateUpdateDeleteResponse

    @Multipart
    @POST("post/{id}")
    suspend fun updatePost(
        @Query("_method") _method: String = "PUT",
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part("price") price: RequestBody,
        @Part("area") area: RequestBody,
        @Part("address") address: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("provinsi_id") provinsi_id: RequestBody,
        @Part("kabupaten_id") kabupaten_id: RequestBody,
        @Part("kecamatan_id") kecamatan_id: RequestBody,
        @Part images: Array<MultipartBody.Part>,
    ): PostGetCreateUpdateDeleteResponse */

    @GET("location/provinsi")
    suspend fun getAllProvinsi(): ProvinsiListResponse

    @GET("location/kabupaten")
    suspend fun getKabupatenByProvinsi(
        @Query("provinsi") provinsi: Int,
    ): KabupatenListResponse

    @GET("location/kecamatan")
    suspend fun getKecamatanByKabupaten(
        @Query("kabupaten") kabupaten: Int,
    ): KecamatanListResponse

    @GET("user/{id}")
    suspend fun getUser(
        @Path("id") id: Int,
    ): UserUpdateResponse

    @Multipart
    @POST("user/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Query("_method") _method: String = "PUT",
        @PartMap params: @JvmSuppressWildcards Map<String,RequestBody>,
        @Part image: @JvmSuppressWildcards MultipartBody.Part,
    ): UserUpdateResponse

    @Multipart
    @POST("user/{id}")
    fun updateUserData(
        @Path("id") id: Int,
        @Query("_method") _method: String = "PUT",
        @PartMap params: @JvmSuppressWildcards Map<String,RequestBody>
    ): UserUpdateResponse

    @POST("login")
    suspend fun login(@Body params: BodyLogin): LoginResponse

    @POST("register")
    suspend fun register(@Body register: BodyRegister): RegisterResponse

    @POST("logout")
    suspend fun logout(): LogoutResponse
}