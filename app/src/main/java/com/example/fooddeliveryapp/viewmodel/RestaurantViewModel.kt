package com.example.fooddeliveryapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.data.api.GeoApiClient
import com.example.fooddeliveryapp.data.model.Restaurant
import com.example.fooddeliveryapp.data.repository.GeoRepository
import com.example.fooddeliveryapp.data.repository.GeoResult
import com.example.fooddeliveryapp.data.repository.RestaurantRepository
import com.example.fooddeliveryapp.data.repository.RestaurantResult
import com.example.fooddeliveryapp.ui.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RestaurantViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val repository = RestaurantRepository()
    private val geoRepository = GeoRepository(GeoApiClient.api)

    private val allRestaurants = mutableListOf<Restaurant>()

    private var page = 1
    private val pageSize = 10

    // ✅ NOT UI STATE
    private var currentLat = 18.5912716
    private var currentLng = 73.738909

    var isLocationInitialized = false
        private set

    // 🔥 LOAD RESTAURANTS
    fun loadRestaurants() {
        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = repository.fetchRestaurants(
                lat = currentLat,
                lng = currentLng
            )) {

                is RestaurantResult.Success -> {
                    allRestaurants.clear()
                    allRestaurants.addAll(result.data)

                    applyFilter()

                    _uiState.update { it.copy(isLoading = false) }
                }

                is RestaurantResult.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    // 🔄 REFRESH
    fun refreshRestaurants() {
        viewModelScope.launch {

            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

            when (val result = repository.fetchRestaurants(
                lat = currentLat,
                lng = currentLng
            )) {

                is RestaurantResult.Success -> {
                    allRestaurants.clear()
                    allRestaurants.addAll(result.data)
                    applyFilter()
                }

                is RestaurantResult.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = result.message)
                    }
                }
            }

            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    // 🥦 VEG FILTER
    fun toggleVegOnly() {
        _uiState.update {
            it.copy(isVegOnly = !it.isVegOnly)
        }
        applyFilter()
    }

    // 🔍 SEARCH
    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }
        applyFilter()
    }

    // 📄 PAGINATION
    fun loadNextPage() {

        val state = _uiState.value

        if (state.isLoading || state.searchQuery.isNotBlank() || state.isVegOnly) return

        _uiState.update { it.copy(isLoading = true) }

        val start = state.restaurants.size
        val end = minOf(start + pageSize, allRestaurants.size)

        if (start < end) {
            val newList = state.restaurants + allRestaurants.subList(start, end)

            _uiState.update {
                it.copy(
                    restaurants = newList,
                    isLoading = false
                )
            }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }

        page++
    }

    fun getRestaurantById(id: String): Restaurant? {
        return allRestaurants.find { it.id == id }
    }

    // 🌍 LOCATION ERROR
    fun updateLocationError(message: String) {
        _uiState.update {
            it.copy(locationError = message)
        }
    }

    // 🔍 SEARCH CITY
    fun searchCityAndUpdate(city: String) {
        viewModelScope.launch {

            _uiState.update { it.copy(isLocationLoading = true) }

            when (val result = geoRepository.getCoordinates(city)) {

                is GeoResult.Success -> {
                    val location = result.data
                    updateLocation(location.lat, location.lon, location.name)
                }

                is GeoResult.Error -> {
                    Log.e("GeoVM", result.message)
                }
            }

            _uiState.update { it.copy(isLocationLoading = false) }
        }
    }

    // 📍 UPDATE LOCATION
    fun updateLocation(lat: Double, lng: Double, name: String = "Current Location") {

        currentLat = lat
        currentLng = lng
        isLocationInitialized = true

        _uiState.update {
            it.copy(locationName = name)
        }

        viewModelScope.launch {
            val address = geoRepository.getAddress(lat, lng)

            _uiState.update {
                it.copy(locationSubtitle = address)
            }
        }

        allRestaurants.clear()
        page = 1

        loadRestaurants()
    }

    // 🧠 FILTER LOGIC
    private fun applyFilter() {

        val state = _uiState.value

        val searchedList = if (state.searchQuery.isBlank()) {
            allRestaurants
        } else {
            allRestaurants.filter { restaurant ->
                restaurant.name.contains(state.searchQuery, true) ||
                        restaurant.cuisines.any {
                            it.contains(state.searchQuery, true)
                        }
            }
        }

        val finalList = if (state.isVegOnly) {
            searchedList.filter { it.isVeg }
        } else {
            searchedList
        }

        if (state.searchQuery.isBlank() && !state.isVegOnly) {
            page = 1
            val paginated = finalList.take(pageSize)

            _uiState.update {
                it.copy(restaurants = paginated)
            }
        } else {
            _uiState.update {
                it.copy(restaurants = finalList)
            }
        }
    }
}