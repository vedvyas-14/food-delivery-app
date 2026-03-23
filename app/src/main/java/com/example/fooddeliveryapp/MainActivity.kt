package com.example.fooddeliveryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddeliveryapp.ui.navigation.NavGraph
import com.example.fooddeliveryapp.ui.theme.FoodDeliveryAppTheme
import com.example.fooddeliveryapp.viewmodel.RestaurantViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            FoodDeliveryAppTheme {

                val viewModel: RestaurantViewModel = viewModel()

                LaunchedEffect(Unit) {
                    viewModel.loadRestaurants()
                }

                NavGraph(
                    viewModel = viewModel,
                    onLoadMore = { viewModel.loadNextPage() }
                )
            }
        }
    }
}