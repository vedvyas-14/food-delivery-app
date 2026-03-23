package com.example.fooddeliveryapp.ui.state

import com.example.fooddeliveryapp.data.model.MenuCategory

sealed interface MenuUiState {

    object Loading : MenuUiState

    data class Success(
        val categories: List<MenuCategory>
    ) : MenuUiState

    data class Error(
        val message: String
    ) : MenuUiState
}