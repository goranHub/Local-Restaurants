package com.sicoapp.localrestaurants.data.local

import androidx.room.ColumnInfo

/**
 * @author ll4
 * @date 1/27/2021
 */
data class RestraurantModel (
    val address: String,
    val latitude: String,
    val longitude: String,
    var name: String
)