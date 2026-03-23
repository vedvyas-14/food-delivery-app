package com.example.fooddeliveryapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.fooddeliveryapp.data.model.Restaurant
import com.example.fooddeliveryapp.ui.screens.HomeScreen
import com.example.fooddeliveryapp.ui.screens.cart.CartScreen
import com.example.fooddeliveryapp.ui.screens.menu.RestaurantMenuScreen
import com.example.fooddeliveryapp.viewmodel.CartViewModel
import com.example.fooddeliveryapp.viewmodel.RestaurantViewModel

@Composable
fun NavGraph(
    viewModel: RestaurantViewModel,
    onLoadMore: () -> Unit
) {

//    val viewModel: RestaurantViewModel = viewModel()
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    //val restaurant = viewModel.getRestaurantById(restaurantId)

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {

            HomeScreen(
                viewModel = viewModel,
                cartViewModel = cartViewModel,
                navController = navController,
                onLoadMore = onLoadMore,
                onRestaurantClick = { restaurantId ->
                    navController.navigate("menu/$restaurantId")
                }
            )
        }

        composable("menu/{restaurantId}") { backStackEntry ->

            val restaurantId =
                backStackEntry.arguments?.getString("restaurantId") ?: ""


            val restaurant = viewModel.getRestaurantById(restaurantId)

            // Find the restaurant object from the list
            //val restaurant = viewModel.restaurants.find { it.id == restaurantId }

            if (restaurant != null) {

                RestaurantMenuScreen(
                    restaurantId = restaurantId,
                    restaurant = restaurant,
                    navController = navController,
                    cartViewModel = cartViewModel
                )
            }
        }

        composable("cart") {
            CartScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }
    }
}