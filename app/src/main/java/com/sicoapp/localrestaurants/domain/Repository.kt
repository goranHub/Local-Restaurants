package com.sicoapp.localrestaurants.domain

import androidx.lifecycle.LiveData
import com.sicoapp.localrestaurants.data.local.DatabaseDataSource
import com.sicoapp.localrestaurants.data.local.Restaurant
import com.sicoapp.localrestaurants.data.remote.NetworkDataSource

/**
 * @author ll4
 * @date 1/26/2021
 */
class Repository(
    private val networkDataSource: NetworkDataSource,
    private val databaseDataSource: DatabaseDataSource
){


    fun fetchRestaurant(): LiveData<List<Restaurant>> {
        return databaseDataSource.getRestaurant()
    }


}