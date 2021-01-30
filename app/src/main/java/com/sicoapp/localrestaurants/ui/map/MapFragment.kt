package com.sicoapp.localrestaurants.ui.map

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import com.sicoapp.localrestaurants.databinding.FragmentCustumMapBinding
import com.sicoapp.localrestaurants.databinding.FragmentMapBinding
import com.sicoapp.localrestaurants.databinding.MapWrapperLayoutBinding
import com.sicoapp.localrestaurants.utils.MapWrapperLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback, View.OnTouchListener {

    //Declare HashMap to store mapping of marker to Activity
    var markerMapId = HashMap<String, String>()

    private val viewModel: MapViewModel by viewModels()
    private lateinit var mMapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    lateinit var mapWrapperLayout: MapWrapperLayout
    lateinit var infoWindow: FragmentCustumMapBinding
    lateinit var infoButton: Button
    lateinit var mMarkerMap: Marker
    private var bgDrawableNormal: Drawable? = null
    private var bgDrawablePressed: Drawable? = null
    private val handler: Handler = Handler()
    private var marker: Marker? = null
    private var pressed = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = FragmentMapBinding.inflate(layoutInflater)

        mMapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!

        mMapFragment.onCreate(savedInstanceState)

        mMapFragment.getMapAsync(this)

        return view.root
    }


    override fun onMapReady(googleMap: GoogleMap) {

        /**
         * Add a marker on the location using the latitude and longitude and move the camera to it.
         */

        mapWrapperLayout = MapWrapperLayoutBinding.inflate(layoutInflater).mapRelativeLayout

        infoWindow = FragmentCustumMapBinding.inflate(layoutInflater)

        mapWrapperLayout.init(googleMap, getPixelsFromDp(requireContext(), 39f + 20f).toInt())

        viewModel.showMapCallback = object : ShowMapCallback {

            override fun onResponse(it: List<RestaurantResponse>) {
                it.map { response ->

                    val position = LatLng(response.latitude, response.longitude)
                    val name = response.name

                    val markerOptions = MarkerOptions()
                    markerOptions.position(position)
                        .title(name)
                        .snippet("I am custom Location Marker.")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                    val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 13f)

                    googleMap.animateCamera(newLatLngZoom)

                    googleMap.setInfoWindowAdapter(MyInfoWindowAdapter(this@MapFragment))

                    mMarkerMap = googleMap.addMarker(markerOptions)

                    mMarkerMap.tag = response

                    val idOne: String = mMarkerMap.id

                    markerMapId[idOne] = response.name

                    mMarkerMap.showInfoWindow()

                    //googleMap.setOnInfoWindowClickListener(this@MapFragment)

                    onClickConfirmed(infoWindow.btAddress, mMarkerMap)

                }
            }
        }
        infoWindow.btAddress.setOnTouchListener(this)
    }

    /**
     * This is called after a successful click
     */
    fun onClickConfirmed(v: View?, marker: Marker?) {
        Toast.makeText(context, marker!!.title + "'s button clicked!", Toast.LENGTH_LONG).show()
    }

    override fun onTouch(view: View, event: MotionEvent?): Boolean {
        if (0 <= event!!.x && event.x <= view.width && 0 <= event.y && event.y <= view.height) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> startPress()
                MotionEvent.ACTION_UP -> handler.postDelayed(confirmClickRunnable, 150)
                MotionEvent.ACTION_CANCEL -> endPress()
                else -> {
                }
            }
        } else {
            endPress()
        }
        return false
    }

    private fun startPress() {
        if (!pressed) {
            pressed = true
            handler.removeCallbacks(confirmClickRunnable)
            view?.background ?: bgDrawablePressed
            if (marker != null) marker!!.showInfoWindow()
        }
    }

    private fun endPress(): Boolean {
        return if (pressed) {
            pressed = false
            handler.removeCallbacks(confirmClickRunnable)
            view?.background ?: bgDrawableNormal
            if (marker != null) marker!!.showInfoWindow()
            true
        } else false
    }

    private val confirmClickRunnable = Runnable {
        if (endPress()) {
            onClickConfirmed(view, marker)
        }
    }

    fun getPixelsFromDp(context: Context, dp: Float): Float {
        val scale: Float = context.resources.displayMetrics.density
        return (dp * scale + 0.5f)
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