package com.example.fooddeliveryapp.data.repository

import com.example.fooddeliveryapp.data.model.Restaurant

sealed class RestaurantResult {

    data class Success(
        val data: List<Restaurant>
    ) : RestaurantResult()

    data class Error(
        val message: String
    ) : RestaurantResult()
}