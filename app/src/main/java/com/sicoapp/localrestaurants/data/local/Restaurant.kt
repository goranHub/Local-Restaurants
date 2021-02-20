package com.sicoapp.localrestaurants.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * @author ll4
 * @date 1/26/2021
 */
@Entity
@Parcelize
data class Restaurant(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "address")
    var address: String,

    @ColumnInfo(name = "latitude")
    var latitude: String,

    @ColumnInfo(name = "longitude")
    var longitude: String,

    @ColumnInfo(name = "name")
    var name: String

) : Parcelable