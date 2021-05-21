package com.sicoapp.localrestaurants.data.remote

import io.reactivex.Single
import javax.inject.Inject

/**
 * @author ll4
 * @date 2/6/2021
 */
class NetworkDataSource @Inject constructor(
    private val restaurantServis: RestaurantServis
) {

    fun fetchRestaurants():  Single<List<Restraurant>>  {
        return restaurantServis.getAll()
    }
}