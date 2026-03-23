package com.example.fooddeliveryapp.ui.screens.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fooddeliveryapp.data.model.Restaurant
import com.example.fooddeliveryapp.ui.components.CartBar
import com.example.fooddeliveryapp.ui.components.FoodItemCard
import com.example.fooddeliveryapp.ui.components.RestaurantHeader
import com.example.fooddeliveryapp.ui.state.MenuUiState
import com.example.fooddeliveryapp.viewmodel.CartViewModel
import com.example.fooddeliveryapp.viewmodel.MenuViewModel
import kotlinx.coroutines.launch
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RestaurantMenuScreen(
    restaurantId: String,
    restaurant: Restaurant,
    navController: NavController,
    cartViewModel: CartViewModel,
    viewModel: MenuViewModel = viewModel()
) {

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }

    // ✅ SINGLE SOURCE OF TRUTH
    var filter by remember { mutableStateOf("ALL") }

    LaunchedEffect(restaurantId) {
        viewModel.loadMenu(
            lat = 18.5912716,
            lng = 73.7389089,
            restaurantId = restaurantId
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        when (uiState) {

            MenuUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MenuUiState.Success -> {

                val state = uiState as MenuUiState.Success

                val isHeaderVisible by remember {
                    derivedStateOf {
                        listState.firstVisibleItemIndex == 0
                    }
                }

                val headerIndexMap = remember(state.categories) {
                    val map = mutableMapOf<Int, Int>()
                    var currentIndex = 1

                    state.categories.forEachIndexed { index, category ->
                        map[index] = currentIndex
                        currentIndex += 1 + category.items.size
                    }
                    map
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        AnimatedVisibility(visible = isHeaderVisible) {
                            RestaurantHeader(restaurant)
                        }

                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 🔹 Category chips
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(state.categories) { index, category ->
                                AssistChip(
                                    onClick = {
                                        coroutineScope.launch {
                                            headerIndexMap[index]?.let {
                                                listState.animateScrollToItem(it)
                                            }
                                        }
                                    },
                                    label = { Text(category.title) }
                                )
                            }
                        }

                        // 🔹 Veg / Non-Veg Filter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            FilterChip(
                                selected = filter == "VEG",
                                onClick = { filter = "VEG" },
                                label = { Text("Veg") }
                            )

                            FilterChip(
                                selected = filter == "NONVEG",
                                onClick = { filter = "NONVEG" },
                                label = { Text("Non-Veg") }
                            )

                            FilterChip(
                                selected = filter == "ALL",
                                onClick = { filter = "ALL" },
                                label = { Text("All") }
                            )
                        }

                        // 🔹 Menu List
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 80.dp
                            )
                        ) {


                            state.categories.forEach { category ->

                                val isExpanded = expandedMap[category.title] ?: true

                                val filteredItems = when (filter) {
                                    "VEG" -> category.items.filter { it.isVeg }
                                    "NONVEG" -> category.items.filter { !it.isVeg }
                                    else -> category.items
                                }

                                if (filteredItems.isNotEmpty()) {

                                    stickyHeader {
                                        Surface(
                                            tonalElevation = 2.dp,
                                            color = MaterialTheme.colorScheme.background
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        expandedMap[category.title] = !isExpanded
                                                    }
                                                    .padding(vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {

                                                Text(
                                                    text = category.title,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.weight(1f)
                                                )

                                                Icon(
                                                    imageVector = if (isExpanded)
                                                        Icons.Default.KeyboardArrowUp
                                                    else
                                                        Icons.Default.KeyboardArrowDown,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }

                                    if (isExpanded) {
                                        items(filteredItems) { item ->
                                            FoodItemCard(
                                                item = item,
                                                cartViewModel = cartViewModel
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 🔹 Floating Cart Bar
                    CartBar(
                        cartViewModel = cartViewModel,
                        onClick = {
                            navController.navigate("cart")
                        },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }

            is MenuUiState.Error -> {

                val state = uiState as MenuUiState.Error

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message)
                }
            }
        }
    }
}