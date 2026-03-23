package com.example.fooddeliveryapp.data.api

import com.example.fooddeliveryapp.data.network.NetworkModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://www.swiggy.com/"

    val api: SwiggyApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(NetworkModule.okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SwiggyApi::class.java)
    }
}