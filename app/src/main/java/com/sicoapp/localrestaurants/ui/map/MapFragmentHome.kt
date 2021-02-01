package com.sicoapp.localrestaurants.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map_home.view.*


@AndroidEntryPoint
class MapFragmentHome : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnMapLoadedCallback {

    private val viewModel: MapViewModel by viewModels()

    companion object {
        private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
        private val markerIconSize = 60
    }

    internal var marker: Marker? = null
    internal var map: GoogleMap? = null
    private var mMapView: MapView? = null

    /*
        internal var customInfoWindow: FragmentCustumMapBinding? = null
    */
    private var customInfoWindow: CustomInfoWindow? = null
    private var isMarkerCenter = true
    private var latLng = LatLng(21.028511, 105.804817)
    lateinit var binding: FragmentMapHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapHomeBinding.inflate(layoutInflater)

        val mapViewBundle: Bundle? = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)
        mMapView = binding.map
        mMapView?.onCreate(mapViewBundle)
        mMapView?.getMapAsync(this)
        return binding.root
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mMapView?.onSaveInstanceState(mapViewBundle)
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap

        viewModel.showMapCallback = object : ShowMapCallback {

            @SuppressLint("ClickableViewAccessibility")
            override fun onResponse(it: List<RestaurantResponse>) {
                it.map { response ->

                    val position = LatLng(response.latitude, response.longitude)
                    val name = response.name

                    val marker = MarkerOptions()

                    marker
                        .position(position)
                        .title(name)
                        .snippet("I am custom Location Marker.")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 15f))
                    map?.setOnCameraMoveListener(this@MapFragmentHome)
                    map?.setOnMapLoadedCallback(this@MapFragmentHome)

                    val mMarkerTag = map?.addMarker(marker)
                    mMarkerTag?.tag = response

                    customInfoWindow = CustomInfoWindow(
                        map,
                        mMarkerTag,
                        requireContext(),
                        binding.baseLayout
                    )

                    customInfoWindow?.onBtnNameClickListener = View.OnTouchListener { v, e ->
                        Toast.makeText(context, "Call btn clicked", Toast.LENGTH_SHORT).show()
                        false
                    }

                    map?.setOnMarkerClickListener {
                        customInfoWindow?.toggleInfo()
                        false
                    }
                }
            }
        }
    }

    override fun onCameraMove() {
        isMarkerCenter = false
        customInfoWindow?.moveToMarkerPosition()
    }

    override fun onMapLoaded() {
        customInfoWindow?.moveToMarkerPosition()
        customInfoWindow?.hideInfo()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }
    override fun onStart() {
        super.onStart()
        mMapView?.onStart()
    }
    override fun onStop() {
        super.onStop()
        mMapView?.onStop()
    }
    override fun onPause() {
        mMapView?.onPause()
        super.onPause()
    }
    override fun onDestroy() {
        mMapView?.onDestroy()
        super.onDestroy()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }
}