package com.sicoapp.localrestaurants.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import com.sicoapp.localrestaurants.ui.BaseFR
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map_home.*
import kotlinx.android.synthetic.main.fragment_map_home.view.*


@AndroidEntryPoint
class MapFragmentHome :
    BaseFR<FragmentMapHomeBinding, BaseActivity>() ,
    OnMapReadyCallback,
    GoogleMap.OnMapLoadedCallback {

    private val viewModel: MapViewModel by viewModels()
    lateinit var name: String
    private lateinit var mMapView: MapView
    override var binding: FragmentMapHomeBinding? = null
    private var map: GoogleMap? = null
    private var customInfoWindowList = mutableListOf<CustomInfoWindow>()
    private var markerList = mutableListOf<MarkerOptions>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = setBinding(inflater, container)
        mMapView = binding!!.map
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)
        return binding!!.root
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true

        viewModel.showMapCallback = object : ShowMapCallback {
            override fun onResponse(it: List<RestaurantResponse>) {
                createMarker(it)
            }
        }
        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(LatLng(45.83758, 16.05111), 14f)
        )
        map?.setOnMapLoadedCallback(this@MapFragmentHome)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onMapLoaded() {


        map?.setOnMarkerClickListener { marker ->
            markerList.map { markerOptions ->

                if (markerOptions.title == marker.title) {
                    customInfoWindowList.map {

                        if (it.marker.title == marker.title) {
                            it.toggleInfo()
                        }
                    }
                }
            }
            false
        }

        customInfoWindowList.map {
                cuw->

            cuw.onBtnNameClickListener =
                View.OnTouchListener { _, _ ->
                    alertDialog("name")
                    showErrorSnackBar("name")
                    Toast.makeText(context, cuw.marker.title, Toast.LENGTH_SHORT).show()
                    false
                }

            cuw.onBtnAddressClickListener =
                View.OnTouchListener { _, _ ->
                    alertDialog("Address")
                    showErrorSnackBar("Address")
                    Toast.makeText(context, cuw.response.address, Toast.LENGTH_SHORT).show()
                    false
                }

            cuw.onBtnLongitudeClickListener =
                View.OnTouchListener { _, _ ->
                    alertDialog("Longitude")
                    showErrorSnackBar("Longitude")
                    Toast.makeText(context, cuw.response.longitude.toString(), Toast.LENGTH_SHORT).show()
                    false
                }

            cuw.onBtnLatitudeClickListener =
                View.OnTouchListener { _, _ ->
                    alertDialog("Latitude")
                    showErrorSnackBar("Latitude")
                    Toast.makeText(context, cuw.response.latitude.toString(), Toast.LENGTH_SHORT).show()
                    false
                }
        }
    }


        @SuppressLint("ClickableViewAccessibility")
        private fun createMarker(mResponseList: List<RestaurantResponse>) {

            mResponseList.forEach { restaurantResponse ->
                val marker = MarkerOptions()

                marker
                    .position(LatLng(restaurantResponse.latitude, restaurantResponse.longitude))
                    .title(restaurantResponse.name)
                    .snippet("I am custom Location Marker.")

                markerList.add(marker)

                customInfoWindowList.add(
                    CustomInfoWindow(
                        map!!,
                        marker,
                        requireContext(),
                        restaurantResponse,
                        binding!!.mapLayout
                    )
                )
            }
        }

        override fun onResume() {
            super.onResume()
            this.mMapView.onResume()
        }

        override fun onStart() {
            super.onStart()
            this.mMapView.onStart()
        }

        override fun onStop() {
            super.onStop()
            this.mMapView.onStop()
        }

        override fun onPause() {
            super.onPause()
            this.mMapView.onPause()
        }

        override fun onDestroy() {
            super.onDestroy()
            mMapView.onDestroy()
        }

        override fun onLowMemory() {
            super.onLowMemory()
            mMapView.onLowMemory()
        }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMapHomeBinding =
        FragmentMapHomeBinding.inflate(inflater, container, false)
    }
