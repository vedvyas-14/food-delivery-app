package com.example.fooddeliveryapp.ui.screens

import android.Manifest
import android.provider.Settings
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.fooddeliveryapp.ui.components.RestaurantCard
import com.example.fooddeliveryapp.viewmodel.RestaurantViewModel
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.fooddeliveryapp.ui.components.CartBar
import com.example.fooddeliveryapp.utils.isLocationEnabled
import com.example.fooddeliveryapp.viewmodel.CartViewModel
import com.google.android.gms.location.LocationServices
import androidx.compose.material3.AlertDialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged


val AppBackground = Color(0xFFF7F7F7)

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: RestaurantViewModel,
    cartViewModel: CartViewModel,
    onLoadMore: () -> Unit,
    navController: NavController,
    onRestaurantClick: (String) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    var showLocationDialog by remember { mutableStateOf(false) }
    var inputLocation by remember { mutableStateOf("") }

    val refreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refreshRestaurants() }
    )

    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.updateLocation(location.latitude, location.longitude)
                } else {
                    // ✅ fallback restored
                    viewModel.updateLocation(18.5912716, 73.738909)
                }
            }
        } else {
            // ✅ fallback restored
            viewModel.updateLocation(18.5912716, 73.738909)
        }
    }

    // 📍 Initial location
    LaunchedEffect(Unit) {

        if (viewModel.isLocationInitialized) return@LaunchedEffect

        if (!isLocationEnabled(context)) {
            viewModel.updateLocationError("Location is turned OFF")
            return@LaunchedEffect
        }

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.updateLocation(location.latitude, location.longitude)
                } else {
                    viewModel.updateLocationError("Unable to fetch location")
                }
            }

        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // 🔁 Pagination
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
            .distinctUntilChanged()
            .collect { lastIndex ->

            if (lastIndex != null &&
                lastIndex >= uiState.restaurants.size - 3 &&
                uiState.searchQuery.isBlank() &&
                !uiState.isVegOnly
            ) {
                onLoadMore()
            }
        }
    }

    // ❌ Error State
    if (uiState.errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize().background(AppBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(text = uiState.errorMessage!!, color = Color.Red)

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { viewModel.loadRestaurants() }) {
                    Text("Retry")
                }
            }
        }
        return
    }

    // ⏳ Loading State
    if (uiState.isLoading && uiState.restaurants.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(AppBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // ✅ MAIN UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .pullRefresh(refreshState)
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            }
    ) {

        // ✅ Location error banner (kept)
        uiState.locationError?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(it)

                TextButton(onClick = {
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) {
                    Text("Turn On")
                }
            }
        }

        // ✅ FULLY RESTORED dialog logic
        if (showLocationDialog) {
            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = { Text("Change da Location boi, no") },//git change test
                text = {
                    Column {

                        Button(
                            onClick = {

                                // 🔴 Step 1: Location enabled check
                                if (!isLocationEnabled(context)) {
                                    coroutineScope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Location is turned OFF",
                                            actionLabel = "Turn On"
                                        )

                                        if (result == SnackbarResult.ActionPerformed) {
                                            context.startActivity(
                                                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                            )
                                        }
                                    }
                                    return@Button
                                }

                                // 🔴 Step 2: Permission
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    return@Button
                                }

                                // 🔴 Step 3: Fetch location
                                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                    if (location != null) {
                                        viewModel.updateLocation(
                                            location.latitude,
                                            location.longitude,
                                            "Current Location"
                                        )
                                        showLocationDialog = false
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Unable to fetch location")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.MyLocation, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Use Current Location")
                        }

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputLocation,
                            onValueChange = { inputLocation = it },
                            placeholder = { Text("Enter city") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (inputLocation.isNotBlank()) {
                            viewModel.searchCityAndUpdate(inputLocation)
                            inputLocation = ""   // ✅ clear input
                            showLocationDialog = false
                        }
                    }) {
                        if (uiState.isLocationLoading) {
                            CircularProgressIndicator(Modifier.size(20.dp))
                        } else Text("Apply")
                    }
                }
            )
        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            stickyHeader {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .zIndex(1f) // ✅ restored
                ) {

                    HeaderBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::updateSearchQuery,
                        vegOnly = uiState.isVegOnly,
                        onVegToggle = viewModel::toggleVegOnly
                    )

                    LocationBar(
                        locationName = uiState.locationName,
                        locationSubtitle = uiState.locationSubtitle,
                        onClick = { showLocationDialog = true }
                    )
                }
            }

            items(
                items = uiState.restaurants,
                key = { it.id }
            ) { restaurant ->

                RestaurantCard(
                    restaurant = restaurant,
                    onRestaurantClick = { id ->
                        viewModel.updateSearchQuery("")
                        onRestaurantClick(id)
                    }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }

        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        CartBar(
            cartViewModel = cartViewModel,
            onClick = { navController.navigate("cart") },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f)
                .padding(bottom = 100.dp)
        )

        if (uiState.restaurants.isEmpty() && uiState.searchQuery.isNotBlank()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results for \"${uiState.searchQuery}\"")
            }
        }
    }
}
@Composable
fun HeaderBar(
    query: String,
    onQueryChange: (String) -> Unit,
    vegOnly: Boolean,
    onVegToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f)
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("VEG", style = MaterialTheme.typography.labelSmall)
            Text("MODE", style = MaterialTheme.typography.labelSmall)

            Switch(
                checked = vegOnly,
                onCheckedChange = { onVegToggle() }
            )
        }
    }
}

@Composable
fun LocationBar(
    locationName: String,
    locationSubtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 📍 Location Icon
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "GPS",
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(10.dp))

            // 🧠 Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = locationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                Text(
                    text = if (locationSubtitle.isBlank()) "Fetching address..."
                    else locationSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

            // ⬇ Dropdown Icon
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand"
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search restaurants") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        modifier = modifier
            .height(52.dp), // ✅ only UI-specific constraint here
        shape = RoundedCornerShape(30.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
    )
}
