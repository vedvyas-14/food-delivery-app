package com.example.fooddeliveryapp.data.repository

import android.util.Log
import com.example.fooddeliveryapp.data.api.SwiggyApi
import com.example.fooddeliveryapp.data.model.MenuCategory
import com.example.fooddeliveryapp.data.model.parser.MenuParser
import com.example.fooddeliveryapp.data.model.Restaurant

class MenuRepository(private val api: SwiggyApi) {

    suspend fun getMenu(
        lat: Double,
        lng: Double,
        restaurantId: String
    ): List<MenuCategory> {

        val response = api.getMenu(
            lat = lat,
            lng = lng,
            restaurantId = restaurantId
        )

        //Log.d("MENU_RESPONSE", response.body().toString())
        Log.d("MENU_DEBUG", "Fetching menu for $restaurantId")


        if (!response.isSuccessful) return emptyList()

        val json = response.body()?.toString() ?: return emptyList()

        return MenuParser.parseMenu(json)
    }
}