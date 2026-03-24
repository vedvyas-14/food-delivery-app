package com.example.fooddeliveryapp.data.mapper

import com.example.fooddeliveryapp.data.local.entity.CartItemEntity
import com.example.fooddeliveryapp.data.model.CartItem

// Entity → Model (for UI)
fun CartItemEntity.toCartItem(): CartItem {
    return CartItem(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl,
        quantity = quantity
    )
}

// Model → Entity (for DB)
fun CartItem.toEntity(imageUrl: String = ""): CartItemEntity {
    return CartItemEntity(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl, // fallback if not available
        quantity = quantity,
        addedAt = System.currentTimeMillis()
    )
}