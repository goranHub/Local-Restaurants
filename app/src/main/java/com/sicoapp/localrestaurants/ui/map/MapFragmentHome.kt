package com.sicoapp.localrestaurants.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import com.sicoapp.localrestaurants.databinding.FragmentDialogWithDataBinding
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import com.sicoapp.localrestaurants.ui.BaseFR
import com.sicoapp.localrestaurants.utils.DialogWithData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map_home.*
import kotlinx.android.synthetic.main.fragment_map_home.view.*


@AndroidEntryPoint
class MapFragmentHome :
    BaseFR<FragmentMapHomeBinding, BaseActivity>() ,
    OnMapReadyCallback,
    GoogleMap.OnMapLoadedCallback {

    private val viewModel: MapViewModel by viewModels()
    private lateinit var mMapView: MapView
    private lateinit var restaurantList : List<RestaurantResponse>
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


        observeViewModel()

        return binding!!.root
    }

    private fun observeViewModel() {
        viewModel.name.observe(viewLifecycleOwner, {
            //
        })
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true

        viewModel.showMapCallback = object : ShowMapCallback {
            override fun onResponse(it: List<RestaurantResponse>) {
                restaurantList = it
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

                    customInfoWindowList.map { cuw->

                        if (cuw.marker.title == marker.title) {
                            cuw.toggleInfo()

                            cuw.onBtnNameClickListener =
                                View.OnTouchListener { _, _ ->
                                    val dialog = DialogWithData()
                                    dialog.texxt = cuw.response.name
                                    dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                                    false
                                }

                            cuw.onBtnAddressClickListener =
                                View.OnTouchListener { _, _ ->
                                    val dialog = DialogWithData()
                                    dialog.texxt = cuw.response.address
                                    dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                                    false
                                }

                            cuw.onBtnLongitudeClickListener =
                                View.OnTouchListener { _, _ ->
                                    val dialog = DialogWithData()
                                    dialog.texxt = cuw.response.longitude.toString()
                                    dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                                    false
                                }

                            cuw.onBtnLatitudeClickListener =
                                View.OnTouchListener { _, _ ->
                                    alertDialog(cuw.response.latitude.toString())
                                    false
                                }
                        }
                    }
                }
            }
            false
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
