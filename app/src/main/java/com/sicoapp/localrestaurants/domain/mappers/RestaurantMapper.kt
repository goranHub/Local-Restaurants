package com.sicoapp.localrestaurants.domain.mappers

import com.sicoapp.localrestaurants.data.local.Restaurant
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse


/**
 * @author ll4
 * @date 2/8/2021
 */
fun List<RestaurantResponse>.mapToRestaurant(): List<Restaurant> {

    var formattedRestaurantList = mutableListOf<Restaurant>()
    val restaurantResponseList = this ?: return formattedRestaurantList

    formattedRestaurantList = restaurantResponseList
        .asSequence()
        .map {
            val name = it.name
            val address = it.address
            val latitude = it.latitude
            val longitude = it.longitude
            Restaurant(
                address = address,
                latitude = latitude.toString(),
                longitude = longitude.toString(),
                name = name
            )
        }.toMutableList()

    return formattedRestaurantList
}