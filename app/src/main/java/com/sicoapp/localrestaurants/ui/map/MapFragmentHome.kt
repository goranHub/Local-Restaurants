package com.sicoapp.localrestaurants.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.MainActivity
import com.sicoapp.localrestaurants.data.local.Restaurant
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import com.sicoapp.localrestaurants.ui.BaseFR
import com.sicoapp.localrestaurants.utils.DialogWithData
import com.sicoapp.localrestaurants.utils.ListenerSubmitData
import com.sicoapp.localrestaurants.utils.hasInternetConnection
import com.sicoapp.localrestaurants.utils.livedata.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragmentHome :
    BaseFR<FragmentMapHomeBinding, BaseActivity>(),
    OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val viewModel: MapViewModel by viewModels()
    private lateinit var mMapView: MapView
    private var listReasturant: List<Restaurant>? = null
    private lateinit var item: Restaurant
    override var binding: FragmentMapHomeBinding? = null
    private var map: GoogleMap? = null

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


        if(checkHasInternetConnection()){
            viewModel.getRestraurants()
            observeRestaurantData()
        }

        listReasturant?.map {
                    map?.addMarker(
                        MarkerOptions()
                            .position(LatLng(it.latitude.toDouble(),
                                             it.longitude.toDouble()))
                            .title(it.name)
                    )
                }

        map?.setOnMarkerClickListener(this)
        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(LatLng(45.83758, 16.05111), 14f)
        )
    }


    override fun onMarkerClick(marker: Marker): Boolean {

        item = listReasturant!!.first { it.name == marker.title }
        var dialog = DialogWithData(item)
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)

        dialog.listener = object : ListenerSubmitData{
            override fun onSubmitData(name: String) {
                item.name = name
                dialog = DialogWithData(item)
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
        }
        return true
    }

    private fun observeRestaurantData() {
        viewModel
            .restaurantData
            .observe(viewLifecycleOwner, { resource ->
                when(resource.status){
                    Status.SUCCESS -> {
                        listReasturant = resource.data
                    }
                    Status.LOADING -> {
                    }
                    Status.ERROR -> {
                    }
                }
            }
        )
    }

    private fun checkHasInternetConnection(): Boolean {
        val mainActivity = activity as MainActivity
        mainActivity.checkInternetConnection()
        return hasInternetConnection(mainActivity)
    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMapHomeBinding =
        FragmentMapHomeBinding.inflate(inflater, container, false)


}
