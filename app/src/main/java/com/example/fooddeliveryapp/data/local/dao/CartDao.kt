package com.example.fooddeliveryapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fooddeliveryapp.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

    @Dao
    interface CartDao {

        // 🔄 Observe cart (for UI updates)
        @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
        fun getCartItems(): Flow<List<CartItemEntity>>

        // 🔍 Get single item
        @Query("SELECT * FROM cart_items WHERE id = :id LIMIT 1")
        suspend fun getItemById(id: String): CartItemEntity?

        // ➕ Insert or update
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertItem(item: CartItemEntity)

        // ❌ Delete item completely
        @Query("DELETE FROM cart_items WHERE id = :id")
        suspend fun deleteItemById(id: String)

        // 🧹 Clear cart
        @Query("DELETE FROM cart_items")
        suspend fun clearCart()

        // 🔼 Increase quantity
        @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE id = :id")
        suspend fun increaseQuantity(id: String)

        // 🔽 Decrease quantity
        @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE id = :id")
        suspend fun decreaseQuantity(id: String)

        // ⚠️ Cleanup invalid items
        @Query("DELETE FROM cart_items WHERE quantity <= 0")
        suspend fun removeZeroQuantityItems()
    }