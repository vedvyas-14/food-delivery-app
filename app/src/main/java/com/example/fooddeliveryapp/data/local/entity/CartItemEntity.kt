package com.example.fooddeliveryapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(

    @PrimaryKey
    val id: String,

    val name: String,
    val price: Double,
    val imageUrl: String,

    val quantity: Int,

    val addedAt: Long = System.currentTimeMillis()
)