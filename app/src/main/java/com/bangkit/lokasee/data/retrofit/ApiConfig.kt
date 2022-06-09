package com.bangkit.lokasee.data.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    const val HOST: String = "http://10.0.2.2:8000"
    var TOKEN: String = ""

    fun getApiService(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = if(TOKEN != ""){
            OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $TOKEN")
                    .build()
                chain.proceed(newRequest)
            }).addInterceptor(loggingInterceptor).build()
        } else {
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("$HOST/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}