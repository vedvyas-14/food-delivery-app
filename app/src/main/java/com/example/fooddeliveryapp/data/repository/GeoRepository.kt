package com.example.fooddeliveryapp.data.repository

import android.util.Log
import com.example.fooddeliveryapp.data.api.GeoApiClient
import com.example.fooddeliveryapp.data.api.GeoApiClient.api
import com.example.fooddeliveryapp.data.api.GeoApiService
import com.example.fooddeliveryapp.data.model.GeoResponseItem
import com.example.fooddeliveryapp.utils.Constants


class GeoRepository(
    private val api: GeoApiService
) {

    suspend fun getCoordinates(city: String): GeoResult {

        return try {
            val response = api.getCoordinates(
                city = city,
                apiKey = Constants.GEO_API_KEY
            )
            if (response.isNotEmpty()) {
                GeoResult.Success(response[0])
            } else {
                GeoResult.Error("No location found")
            }

        } catch (e: Exception) {
//            Log.e("GeoRepository", "Error: ${e.message}")
//            Log.d("GEO_DEBUG", "City=$city API=${Constants.GEO_API_KEY}")
            GeoResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getAddress(lat: Double, lon: Double): String {

        return try {
            val response = GeoApiClient.api.getAddressFromCoordinates(
                lat = lat,
                lon = lon,
                apiKey = Constants.GEO_API_KEY
            )

            if (response.isNotEmpty()) {
                val place = response[0]

                place.name // or "${place.name}"
            } else {
                "Unknown location"
            }

        } catch (e: Exception) {
            Log.e("GeoRepository", "Reverse error: ${e.message}")
            "Unknown location"
        }
    }

}


sealed class GeoResult {
    data class Success(val data: GeoResponseItem) : GeoResult()
    data class Error(val message: String) : GeoResult()
}