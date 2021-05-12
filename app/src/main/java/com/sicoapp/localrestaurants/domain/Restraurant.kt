package com.sicoapp.localrestaurants.domain

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
    var latitude: String,
    @SerializedName("Longitude")
    var longitude: String,
    @SerializedName("Name")
    var name: String
) : Parcelable