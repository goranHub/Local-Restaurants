package com.sicoapp.localrestaurants.ui.map

import android.app.Activity
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import kotlinx.android.synthetic.main.fragment_custum_map.view.*

/**
 * @author ll4
 * @date 1/29/2021
 */
class CustomInfoWindowGoogleMap(val mapfrag: MapFragment) : GoogleMap.InfoWindowAdapter {


    override fun getInfoContents(marker: Marker?): View {

        val mInfoView = (mapfrag.requireActivity() as Activity)
            .layoutInflater
            .inflate(
            R.layout.fragment_custum_map,
            null
        )

        val mInfoWindow: RestaurantResponse? = marker?.tag as RestaurantResponse?

        mInfoView.tv_name.text = mInfoWindow?.name
        mInfoView.tv_address.text = mInfoWindow?.address
        mInfoView.tv_latitude.text = mInfoWindow?.latitude.toString()
        mInfoView.tv_longitude.text = mInfoWindow?.longitude.toString()

        return mInfoView
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
}