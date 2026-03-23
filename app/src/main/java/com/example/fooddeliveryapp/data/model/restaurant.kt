package com.example.fooddeliveryapp.data.model
import androidx.compose.runtime.Immutable

@Immutable
data class Restaurant(
    val id: String,
    val name: String,
    val locality: String,
    val areaName: String,
    val cuisines: List<String>,
    val rating: Double,
    val imageId: String,
    val deliveryTime: Int,
    val isVeg: Boolean
) {
    val imageUrl: String
        get() = "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/$imageId"
}