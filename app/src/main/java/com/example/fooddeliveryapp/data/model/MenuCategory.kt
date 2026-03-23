package com.example.fooddeliveryapp.data.model

import androidx.compose.runtime.Immutable


@Immutable
data class MenuCategory(
    val title: String,
    val items: List<MenuItem>
)