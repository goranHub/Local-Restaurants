package com.sicoapp.localrestaurants.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map_home.*
import kotlinx.android.synthetic.main.fragment_map_home.view.*


@AndroidEntryPoint
class MapFragmentHome :
    Fragment(R.layout.fragment_map_home),
    OnMapReadyCallback,
    GoogleMap.OnMapLoadedCallback {

    private val viewModel: MapViewModel by viewModels()
    private var map: GoogleMap? = null
    private var customInfoWindow1: CustomInfoWindow? = null
    private var customInfoWindow2: CustomInfoWindow? = null
    lateinit var marker1: MarkerOptions
    lateinit var marker2: MarkerOptions
    lateinit var mResponse: RestaurantResponse
    lateinit var binding: FragmentMapHomeBinding
    private lateinit var mMapView: MapView
    private lateinit var customInfoWindowList: ArrayList<CustomInfoWindow>
    private val latlngs: ArrayList<LatLng> = ArrayList()
    private val title: ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapHomeBinding.inflate(layoutInflater)
        mMapView = binding.map
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        map = googleMap

        viewModel.showMapCallback = object : ShowMapCallback {
            @SuppressLint("ClickableViewAccessibility")
            override fun onResponse(it: List<RestaurantResponse>) {

                /*     it.map { response ->
                         mResponse = response
                         val position = LatLng(response.latitude, response.longitude)
                         val name = response.name
                     }*/

                createMarker()

                map?.animateCamera(
                    CameraUpdateFactory
                        .newLatLngZoom(marker1.position, 17f)
                )

                map?.setOnMapLoadedCallback(this@MapFragmentHome)

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapLoaded() {

        map?.setOnMarkerClickListener {
            customInfoWindow1?.toggleInfo()
            false
        }

        customInfoWindow2 = CustomInfoWindow(
            map!!,
            marker2,
            requireContext(),
            binding.mapLayout
        )

        customInfoWindow1 = CustomInfoWindow(
            map!!,
            marker1,
            requireContext(),
            binding.mapLayout
        )

        customInfoWindow1?.onBtnNameClickListener = View.OnTouchListener { _, _ ->
            Toast.makeText(context, "Call btn clicked", Toast.LENGTH_SHORT).show()
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createMarker() {

        customInfoWindow2?.onBtnNameClickListener = View.OnTouchListener { _, _ ->
            Toast.makeText(context, "Call btn clicked", Toast.LENGTH_SHORT).show()
            false
        }

        marker1 = MarkerOptions()
        marker1
            .position(LatLng(45.80297, 16.00064))
            .title("Bufalow Sam")
            .snippet("193-6569 Ut, St.")

        marker2 = MarkerOptions()
        marker2
            .position(LatLng(45.80297, 16.00121))
            .title("9724 Inceptos Ave")
            .snippet("193-6569 Ut, St.")

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


}