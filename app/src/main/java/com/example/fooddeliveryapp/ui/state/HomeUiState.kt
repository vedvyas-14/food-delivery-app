package com.example.fooddeliveryapp.ui.state

import android.location.Location
import com.example.fooddeliveryapp.data.model.Restaurant


data class HomeUiState(

    // 🔄 Loading + Error
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,

    // 🍽 Data
    val restaurants: List<Restaurant> = emptyList(),

    // 🔍 Search + Filters
    val searchQuery: String = "",
    val isVegOnly: Boolean = false,

    // 📍 Location
    val location: Location? = null,
    val locationName: String = "",
    val locationSubtitle: String = "",
    val locationError: String? = null,
    val isLocationLoading: Boolean = false
)