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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map.*


@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback{

    private val viewModel: MapViewModel by viewModels()
    lateinit var mMapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mMapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mMapFragment.onCreate(savedInstanceState)
        mMapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        /**
         * Add a marker on the location using the latitude and longitude and move the camera to it.
         */
        viewModel.showMapCallback = object : ShowMapCallback {

            override fun onResponse(it: List<RestaurantResponse>) {
                it.map { response ->

                    val position = LatLng(response.latitude, response.longitude)
                    val name = response.name

                    val info  = response

                    val markerOptions = MarkerOptions()
                    markerOptions.position(position)
                        .title(name)
                        .snippet("I am custom Location Marker.")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                    val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 13f)
                    googleMap.animateCamera(newLatLngZoom)
                    googleMap.setInfoWindowAdapter(CustomInfoWindowGoogleMap(this@MapFragment ))

                    val marker = googleMap.addMarker(markerOptions)
                    marker.tag = info
                    marker.showInfoWindow()
                }
            }
        }
    }

    override fun onResume() {
        mMapFragment.onResume()
        super.onResume()
    }

    override fun onPause() {
        mMapFragment.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapFragment.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mMapFragment.onLowMemory()
        super.onLowMemory()
    }

}