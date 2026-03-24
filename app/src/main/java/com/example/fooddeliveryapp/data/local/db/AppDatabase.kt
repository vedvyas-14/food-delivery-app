package com.example.fooddeliveryapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fooddeliveryapp.data.local.dao.CartDao
import com.example.fooddeliveryapp.data.local.entity.CartItemEntity

@Database(
    entities = [CartItemEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao
}