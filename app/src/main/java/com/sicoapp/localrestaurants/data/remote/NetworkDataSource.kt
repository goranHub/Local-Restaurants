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

    private lateinit var responseRes : List<Restraurant>

/*    fun fetchRestaurants():  Single<List<Restraurant>>  {

        restaurantServis.getAll().enqueue(object : Callback<List<Restraurant>>{
            override fun onResponse(
                call: Call<List<Restraurant>>,
                response: Response<List<Restraurant>>
            ) {
                responseRes = response.body() ?: return
            }

            override fun onFailure(call: Call<List<Restraurant>>, t: Throwable) {
                Log.d("error", "onFailure ${t.localizedMessage}")
            }
        })
        return Single.just(responseRes)
    }*/

    // orginal api call
    fun fetchRestaurants():  Single<List<Restraurant>>  {
        return restaurantServis.getAll()
    }
}