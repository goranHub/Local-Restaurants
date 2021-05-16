package com.sicoapp.localrestaurants.data.remote

import com.sicoapp.localrestaurants.domain.Restraurant
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

/**
 * @author ll4
 * @date 1/26/2021
 */

interface RestaurantServis {

    @GET("54ef80f5a11ac4d607752717")
    fun getAll():  Single<List<Restraurant>>

}