package com.example.fooddeliveryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.data.api.RetrofitClient
import com.example.fooddeliveryapp.data.repository.MenuRepository
import com.example.fooddeliveryapp.ui.state.MenuUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {

    private val repository = MenuRepository(RetrofitClient.api)

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState

    fun loadMenu(
        lat: Double,
        lng: Double,
        restaurantId: String
    ) {

        viewModelScope.launch {

            try {

                val categories = repository.getMenu(
                    lat = lat,
                    lng = lng,
                    restaurantId = restaurantId
                )

                //Log.d("MENU_DEBUG", "Categories parsed: ${categories.size}")

                _uiState.value = MenuUiState.Success(categories)

            } catch (e: Exception) {

                _uiState.value =
                    MenuUiState.Error(e.message ?: "Error")

            }
        }
    }
}