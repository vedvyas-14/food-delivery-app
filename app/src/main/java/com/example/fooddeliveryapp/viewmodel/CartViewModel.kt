package com.example.fooddeliveryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.data.model.CartItem
import com.example.fooddeliveryapp.data.repository.CartRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val totalItems: Int = 0
)

class CartViewModel(
    private val repository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CartUiState())
    val state: StateFlow<CartUiState> = _state

    init {
        viewModelScope.launch {
            repository.cartItems.collect { items ->

                _state.value = CartUiState(
                    items = items,
                    totalPrice = items.sumOf { it.price * it.quantity },
                    totalItems = items.sumOf { it.quantity }
                )
            }
        }
    }

    fun addItem(
        id: String,
        name: String,
        price: Double,
        imageUrl: String
    ) {
        viewModelScope.launch {
            repository.addItem(
                CartItem(id, name, price, 1, imageUrl)
            )
        }
    }

    fun removeItem(id: String) {
        viewModelScope.launch {
            repository.removeItem(id)
        }
    }

    fun removeItemCompletely(id: String) {
        viewModelScope.launch {
            repository.removeItemCompletely(id)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}