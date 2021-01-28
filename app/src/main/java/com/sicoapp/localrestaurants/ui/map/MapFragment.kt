package com.sicoapp.localrestaurants.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.local.RestraurantModel
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()
    private var restaurantPlaces: RestraurantModel? = null
    lateinit var mMapFragment: SupportMapFragment

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_map, container, false)

        mMapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mMapFragment.getMapAsync(this)

        return v
    }


    override fun onMapReady(googleMap: GoogleMap) {
        /**
         * Add a marker on the location using the latitude and longitude and move the camera to it.
         */

        viewModel.showMapCallback = object : ShowMapCallback {
            override fun onResponse(it: List<RestaurantResponse>) {
                 it.map {
                     val position = LatLng(it.latitude, it.longitude)
                     val name = it.name
                     googleMap.addMarker(MarkerOptions().position(position).title(name))
                     val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 13f)
                     googleMap.animateCamera(newLatLngZoom)
                 }
            }
        }

    }
}