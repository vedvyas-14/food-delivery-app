package com.example.fooddeliveryapp.data.model
import androidx.compose.runtime.Immutable

@Immutable
data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int
)