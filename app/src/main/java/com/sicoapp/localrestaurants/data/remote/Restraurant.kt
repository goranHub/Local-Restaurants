package com.sicoapp.localrestaurants.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * @author ll4
 * @date 5/7/2021
 */
@Parcelize
data class Restraurant(
    @SerializedName("Address")
    var address: String,
    @SerializedName("Latitude")
    var latitude: Double,
    @SerializedName("Longitude")
    var longitude: Double,
    @SerializedName("Name")
    var name: String,
    var photoTaken : Boolean
) : Parcelable