package com.example.fooddeliveryapp.data.api

import com.example.fooddeliveryapp.data.model.GeoResponseItem
import com.example.fooddeliveryapp.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoApiService {

    @GET("geo/1.0/direct")
    suspend fun getCoordinates(
        @Query("q") city: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<GeoResponseItem>

    @GET("geo/1.0/reverse")
    suspend fun getAddressFromCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<GeoResponseItem>
}

