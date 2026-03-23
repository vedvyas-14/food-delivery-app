package com.example.fooddeliveryapp.data.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SwiggyApi {

    @GET("dapi/restaurants/list/v5")
    suspend fun getRestaurants(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("page_type") pageType: String = "DESKTOP_WEB_LISTING"
    ): Response<JsonObject>


    @GET("mapi/menu/pl")
    suspend fun getMenu(
        @Query("page-type") pageType: String = "REGULAR_MENU",
        @Query("complete-menu") completeMenu: Boolean = true,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("restaurantId") restaurantId: String,
        @Query("submitAction") submitAction: String = "ENTER"
    ): Response<JsonObject>

}