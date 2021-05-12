package com.sicoapp.localrestaurants.domain.mappers

import com.sicoapp.localrestaurants.data.local.RestaurantEntity
import com.sicoapp.localrestaurants.domain.Restraurant

/**
 * @author ll4
 * @date 5/7/2021
 */
object DataMapper {

    fun mapEntitiesToDomain(input: List<RestaurantEntity>): List<Restraurant> =
        input.map {
            Restraurant(
                address = it.address,
                latitude = it.latitude,
                longitude = it.longitude,
                name = it.name
            )
        }



    fun mapToSingleRestaurantEntity(res : Restraurant): RestaurantEntity{
      return  RestaurantEntity(
            address = res.address,
            latitude = res.latitude,
            longitude = res.longitude,
            name = res.name
        )
    }

}

fun List<Restraurant>.mapToRestaurantEntity(): List<RestaurantEntity> {

    var formattedRestaurantList = mutableListOf<RestaurantEntity>()
    val restaurantResponseList = this ?: return formattedRestaurantList

    formattedRestaurantList = restaurantResponseList
        .asSequence()
        .map {

            val name = it.name
            val address = it.address
            val latitude = it.latitude
            val longitude = it.longitude
            RestaurantEntity(
                address = address,
                latitude = latitude,
                longitude = longitude,
                name = name
            )
        }.toMutableList()

    return formattedRestaurantList
}