package com.sicoapp.localrestaurants.ui.map

import android.app.Activity
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import kotlinx.android.synthetic.main.fragment_custum_map.view.*
import timber.log.Timber

/**
 * @author ll4
 * @date 1/29/2021
 */
class MyInfoWindowAdapter(private val mapFragment: MapFragment) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker?): View {

        val mInfoView = (mapFragment.requireActivity() as Activity).layoutInflater.inflate(R.layout.fragment_custum_map,null)
        val response: RestaurantResponse? = marker?.tag as RestaurantResponse?

        mInfoView.bt_name.text = response?.name
        mInfoView.bt_address.text = response?.address
        mInfoView.bt_latitude.text = response?.latitude.toString()
        mInfoView.bt_longitude.text = response?.longitude.toString()

        mInfoView.bt_name.setOnClickListener {
            Timber.d("getContext()getContext()")
        }

        return mInfoView
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
}