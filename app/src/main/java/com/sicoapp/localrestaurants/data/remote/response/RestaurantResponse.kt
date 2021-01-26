package com.sicoapp.localrestaurants.data.remote.response

import com.google.gson.annotations.SerializedName


data class RestaurantResponse(
    @SerializedName("Address")
    val address: String,
    @SerializedName("Latitude")
    val latitude: Double,
    @SerializedName("Longitude")
    val longitude: Double,
    @SerializedName("Name")
    val name: String
)