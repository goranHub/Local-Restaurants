package com.sicoapp.localrestaurants.ui.map

import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse

/**
 * @author ll4
 * @date 1/28/2021
 */
interface ShowMapCallback {

    fun onResponse(it : List<RestaurantResponse>)

}