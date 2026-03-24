package com.example.fooddeliveryapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.fooddeliveryapp.data.local.db.DatabaseProvider
import com.example.fooddeliveryapp.data.model.Restaurant
import com.example.fooddeliveryapp.data.repository.CartRepository
import com.example.fooddeliveryapp.ui.screens.HomeScreen
import com.example.fooddeliveryapp.ui.screens.cart.CartScreen
import com.example.fooddeliveryapp.ui.screens.menu.RestaurantMenuScreen
import com.example.fooddeliveryapp.viewmodel.CartViewModel
import com.example.fooddeliveryapp.viewmodel.CartViewModelFactory
import com.example.fooddeliveryapp.viewmodel.RestaurantViewModel

@Composable
fun NavGraph(
    viewModel: RestaurantViewModel,
    onLoadMore: () -> Unit
) {

//    val viewModel: RestaurantViewModel = viewModel()
    val navController = rememberNavController()

    //val restaurant = viewModel.getRestaurantById(restaurantId)

    val context = LocalContext.current

    val db = remember {
        DatabaseProvider.getDatabase(context.applicationContext)
    }

    val repository = remember {
        CartRepository(db.cartDao())
    }

    val factory = remember {
        CartViewModelFactory(repository)
    }

    val cartViewModel: CartViewModel = viewModel(factory = factory)

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