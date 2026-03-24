package com.example.fooddeliveryapp.data.repository

import com.example.fooddeliveryapp.data.local.dao.CartDao
import com.example.fooddeliveryapp.data.mapper.toCartItem
import com.example.fooddeliveryapp.data.mapper.toEntity
import com.example.fooddeliveryapp.data.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepository(
    private val dao: CartDao
) {

    // 🔄 Observe cart (Entity → Model)
    val cartItems: Flow<List<CartItem>> =
        dao.getCartItems().map { list ->
            list.map { it.toCartItem() }
        }

    // ➕ Add item (your existing logic moved here)
    suspend fun addItem(item: CartItem, imageUrl: String = "") {

        val existingItem = dao.getItemById(item.id)

        if (existingItem != null) {
            dao.increaseQuantity(item.id)
        } else {
            dao.insertItem(item.toEntity(imageUrl))
        }
    }

    // ➖ Remove item (decrease or delete)
    suspend fun removeItem(id: String) {

        val existingItem = dao.getItemById(id)

        if (existingItem != null) {

            if (existingItem.quantity > 1) {
                dao.decreaseQuantity(id)
            } else {
                dao.deleteItemById(id)
            }
        }

        dao.removeZeroQuantityItems()
    }

    // ❌ Remove completely
    suspend fun removeItemCompletely(id: String) {
        dao.deleteItemById(id)
    }

    // 🧹 Clear cart
    suspend fun clearCart() {
        dao.clearCart()
    }
}