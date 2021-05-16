package com.sicoapp.localrestaurants.data.remote

import android.util.Log
import com.sicoapp.localrestaurants.domain.Restraurant
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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