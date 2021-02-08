package com.sicoapp.localrestaurants.domain.mappers

import com.sicoapp.localrestaurants.data.local.Restaurant
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse

/**
 * @author ll4
 * @date 2/8/2021
 */
fun RestaurantResponse.mapToRestaurant() : Restaurant{
    return Restaurant(
        address = this.address,
        latitude = this.latitude.toString(),
        longitude = this.longitude.toString(),
        name = this.name

    )
}