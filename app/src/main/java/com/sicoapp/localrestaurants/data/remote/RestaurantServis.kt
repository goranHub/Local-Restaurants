package com.sicoapp.localrestaurants.data.remote

import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author ll4
 * @date 1/26/2021
 */
interface RestaurantServis {

    //TODO
    @GET("v2/54ef80f5a11ac4d607752717")
    fun get(
        @Query("id") id: Int
    ): RestaurantResponse

}