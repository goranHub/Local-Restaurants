package com.sicoapp.localrestaurants.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author ll4
 * @date 1/26/2021
 */
@Entity
data class Restaurant (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "latitude")
    val latitude: String,

    @ColumnInfo(name = "longitude")
    val longitude: String,

    @ColumnInfo(name = "name")
    val name: String
)