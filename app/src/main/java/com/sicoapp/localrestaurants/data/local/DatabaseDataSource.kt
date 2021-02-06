package com.sicoapp.localrestaurants.data.local

import javax.inject.Inject

/**
 * @author ll4
 * @date 2/6/2021
 */
class DatabaseDataSource @Inject constructor(
    private val databaseDao : DatabaseDao
){
    fun saveRestaurant(restaurant : Restaurant){
        databaseDao.saveRestaurant(restaurant)
    }

    fun getRestaurant() = databaseDao.getRestaurant()
}