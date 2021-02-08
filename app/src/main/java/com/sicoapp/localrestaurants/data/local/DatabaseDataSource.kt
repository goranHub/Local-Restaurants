package com.sicoapp.localrestaurants.data.local

import javax.inject.Inject

/**
 * @author ll4
 * @date 2/6/2021
 */
class DatabaseDataSource @Inject constructor(
    private val databaseDao : DatabaseDao
){
    fun saveRestaurantByName(restaurant : Restaurant){
        databaseDao.insertAll(restaurant)
    }

    fun getRestaurant() = databaseDao.getAll()

    fun saveRestaurants(item : Restaurant) {
        databaseDao.insertAll(item)
    }
}