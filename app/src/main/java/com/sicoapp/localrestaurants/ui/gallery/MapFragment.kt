package com.sicoapp.localrestaurants.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.local.RestraurantModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map.*

@AndroidEntryPoint
class MapFragment :  Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()
    private var restaurantPlaces: RestraurantModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return super.onCreateView(inflater, container, savedInstanceState)
    }



    override fun onMapReady(googleMap: GoogleMap) {
        /**
         * Add a marker on the location using the latitude and longitude and move the camera to it.
         */
        val position = LatLng(
            restaurantPlaces!!.latitude.toDouble(),
            restaurantPlaces!!.longitude.toDouble()
        )

        //googleMap.addMarker(MarkerOptions().position(position).title(restaurantPlaces!!.location))
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
        googleMap.animateCamera(newLatLngZoom)
    }

}