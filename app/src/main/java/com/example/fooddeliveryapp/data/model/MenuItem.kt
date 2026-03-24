package com.example.fooddeliveryapp.data.model

import androidx.compose.runtime.Immutable


@Immutable
data class MenuItem(
    val id: String,
    val name: String,
    val price: Int,
    val description: String?,
    val imageUrl: String,
    val isVeg: Boolean
)