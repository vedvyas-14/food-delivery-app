package com.example.fooddeliveryapp.data.repository


import com.example.fooddeliveryapp.data.api.RetrofitClient
import com.example.fooddeliveryapp.data.model.Restaurant
import com.google.gson.JsonObject

class RestaurantRepository {

    suspend fun fetchRestaurants(
        lat: Double,
        lng: Double
    ): RestaurantResult {


        return try {

            val response = RetrofitClient.api.getRestaurants(lat, lng)

            if (response.isSuccessful) {

                val body = response.body()

                if (body != null) {
                    RestaurantResult.Success(parseRestaurants(body))
                } else {
                    RestaurantResult.Error("Empty response from server")
                }

            } else {
                RestaurantResult.Error("Server error: ${response.code()}")
            }

        } catch (e: java.net.SocketTimeoutException) {
            RestaurantResult.Error(e.message ?:"Request timed out")
        } catch (e: java.io.IOException) {
            RestaurantResult.Error(e.message ?:"No internet connection")
        } catch (e: Exception) {
            RestaurantResult.Error(e.message ?:"Something went wrong")
        }
    }


    private fun parseRestaurants(json: JsonObject): List<Restaurant> {

        val restaurantMap = mutableMapOf<String, Restaurant>()

        val cards = json
            .getAsJsonObject("data")
            .getAsJsonArray("cards")

        for (cardElement in cards) {

            val cardObject = cardElement
                .asJsonObject
                .getAsJsonObject("card")
                ?.getAsJsonObject("card")
                ?: continue

            val gridElements = cardObject
                .getAsJsonObject("gridElements")
                ?.getAsJsonObject("infoWithStyle")
                ?: continue

            val restaurantsArray = gridElements
                .getAsJsonArray("restaurants")
                ?: continue

            for (restaurantElement in restaurantsArray) {

                val info = restaurantElement
                    .asJsonObject
                    .getAsJsonObject("info")

                val id = info.get("id").asString


                if (restaurantMap.containsKey(id)) continue

                val isVeg = if (info.has("veg")) {
                    info.get("veg").asBoolean
                } else {
                    false
                }

                val restaurant = Restaurant(
                    id = id,
                    name = info.get("name").asString,
                    locality = info.get("locality").asString,
                    areaName = info.get("areaName").asString,
                    cuisines = info.getAsJsonArray("cuisines").map { it.asString },
                    rating = info.get("avgRating").asDouble,
                    imageId = info.get("cloudinaryImageId").asString,
                    deliveryTime = info.getAsJsonObject("sla")
                        .get("deliveryTime").asInt,
                    isVeg = isVeg
                )
                //Log.d("VEG_DEBUG", "${info.get("name").asString} → ${isVeg}")

                restaurantMap[id] = restaurant
            }
        }


        return restaurantMap.values.toList()
    }
}