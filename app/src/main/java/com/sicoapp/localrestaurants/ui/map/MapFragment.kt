package com.sicoapp.localrestaurants.ui.map

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_progress.*
import kotlinx.android.synthetic.main.fragment_map.*
import timber.log.Timber


@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private val viewModel: MapViewModel by viewModels()
    lateinit var mMapFragment: SupportMapFragment


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
                it.map {

                    val position = LatLng(it.latitude, it.longitude)
                    val name = it.name
                    googleMap.addMarker(MarkerOptions().position(position).title(name))
                    val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 13f)
                    googleMap.animateCamera(newLatLngZoom)

                }
            }
        }

        googleMap.setOnMarkerClickListener(this@MapFragment)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val locAddress = marker!!.title
        alertDialog(locAddress)
        Timber.d(locAddress)
        return true
    }

    private fun alertDialog(name: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Restaurant")
        builder.setMessage(name)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(
                requireContext(),
                android.R.string.yes, Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(
                requireContext(),
                android.R.string.no, Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNeutralButton("Maybe") { dialog, which ->
            Toast.makeText(
                requireContext(),
                "Maybe", Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
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