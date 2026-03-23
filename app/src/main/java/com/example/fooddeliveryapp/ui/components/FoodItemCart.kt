package com.example.fooddeliveryapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.fooddeliveryapp.data.model.MenuItem
import com.example.fooddeliveryapp.viewmodel.CartViewModel

@Composable
fun FoodItemCard(
    item: MenuItem,
    cartViewModel: CartViewModel
) {

    val cartItem by remember(cartViewModel.cartItems) {
        derivedStateOf {
            cartViewModel.cartItems.find { it.id == item.id }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {

                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "₹${item.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!item.description.isNullOrBlank()) {

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(50),
                tonalElevation = 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                color = MaterialTheme.colorScheme.background
            ) {

                val currentItem = cartItem // ✅ snapshot

                if (currentItem == null) {

                    // 🔹 ADD Button
                    Text(
                        text = "ADD",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                cartViewModel.addItem(
                                    id = item.id,
                                    name = item.name,
                                    price = item.price.toDouble()
                                )
                            }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                } else {

                    // 🔹 Quantity Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {

                        Text(
                            text = "-",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { cartViewModel.removeItem(item.id) }
                                .padding(8.dp)
                        )

                        Text(
                            text = currentItem.quantity.toString(), // ✅ FIXED
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "+",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    cartViewModel.addItem(
                                        item.id,
                                        item.name,
                                        item.price.toDouble()
                                    )
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}