package com.example.fooddeliveryapp.data.model

import androidx.compose.runtime.Immutable


@Immutable
data class GeoResponseItem(
    val lat: Double,
    val lon: Double,
    val name: String,
    val country: String,
    val state: String?
)