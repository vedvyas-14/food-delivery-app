package com.example.fooddeliveryapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.fooddeliveryapp.data.model.CartItem

class CartViewModel : ViewModel() {

    var cartItems = mutableStateListOf<CartItem>()
        private set

    fun addItem(id: String, name: String, price: Double) {

        val existingItem = cartItems.find { it.id == id }

        if (existingItem != null) {
            cartItems.replaceAll {
                if (it.id == id) {
                    it.copy(quantity = it.quantity + 1)
                } else it
            }
        } else {
            cartItems.add(
                CartItem(
                    id = id,
                    name = name,
                    price = price,
                    quantity = 1
                )
            )
        }
    }

    fun removeItem(id: String) {

        val existingItem = cartItems.find { it.id == id }

        if (existingItem != null) {

            if (existingItem.quantity > 1) {
                cartItems.replaceAll {
                    if (it.id == id) {
                        it.copy(quantity = it.quantity - 1)
                    } else it
                }
            } else {
                cartItems.remove(existingItem)
            }
        }
    }

    fun removeItemCompletely(id: String) {
        cartItems.removeAll { it.id == id }
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.price * it.quantity }
    }

    fun getTotalItems(): Int {
        return cartItems.sumOf { it.quantity }
    }
}
