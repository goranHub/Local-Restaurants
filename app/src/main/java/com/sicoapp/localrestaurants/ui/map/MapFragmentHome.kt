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
import com.sicoapp.localrestaurants.data.local.Restaurant
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import com.sicoapp.localrestaurants.ui.BaseFR
import com.sicoapp.localrestaurants.utils.DialogEditData
import com.sicoapp.localrestaurants.utils.DialogWithData
import com.sicoapp.localrestaurants.utils.ListenerSubmitData
import com.sicoapp.localrestaurants.utils.livedata.Status
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


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

        Timber.d(" 1 ")

        Timber.d(" 2 ")

        observeRestaurantData(map)

        map?.uiSettings?.isZoomControlsEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true

        map?.setOnMarkerClickListener(this)
        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(LatLng(45.83758, 16.05111), 14f)
        )

        Timber.d("3")
    }


    override fun onMarkerClick(marker: Marker): Boolean {

        item = listReasturant!!.first { it.name == marker.title }

        val dialogWithData = DialogWithData(item)

        dialogWithData.show(requireActivity().supportFragmentManager, dialogWithData.tag)

        dialogWithData.listener = object : DialogWithData.ListenerClicked {

            override fun onButton(type: String) {

                if (type == "latitude") {
                    val restaurant = dialogWithData.restaurant
                    val dialogEdit = DialogEditData(restaurant.latitude)

                    dialogEdit.show(requireActivity().supportFragmentManager, dialogWithData.tag)
                    dialogEdit.listener = object : ListenerSubmitData {
                        override fun onSubmitData(submitData: String) {
                            item.latitude = submitData
                            viewModel.update(item)

                            val dialog = DialogWithData(item)
                            dialog.show(requireActivity().supportFragmentManager, dialog.tag)

                            dialogEdit.dismiss()
                            dialogWithData.dismiss()

                        }
                    }
                }

                if (type == "longitude") {
                    val restaurant = dialogWithData.restaurant
                    val dialogEdit = DialogEditData(restaurant.longitude)

                    dialogEdit.show(requireActivity().supportFragmentManager, dialogWithData.tag)
                    dialogEdit.listener = object : ListenerSubmitData {
                        override fun onSubmitData(submitData: String) {
                            item.longitude = submitData
                            viewModel.update(item)

                            val dialog = DialogWithData(item)
                            dialog.show(requireActivity().supportFragmentManager, dialog.tag)

                            dialogEdit.dismiss()
                            dialogWithData.dismiss()
                        }
                    }
                }

                if (type == "address") {
                    val restaurant = dialogWithData.restaurant
                    val dialogEdit = DialogEditData(restaurant.address)

                    dialogEdit.show(requireActivity().supportFragmentManager, dialogWithData.tag)
                    dialogEdit.listener = object : ListenerSubmitData {
                        override fun onSubmitData(submitData: String) {
                            item.address = submitData
                            viewModel.update(item)

                            val dialog = DialogWithData(item)
                            dialog.show(requireActivity().supportFragmentManager, dialog.tag)

                            dialogEdit.dismiss()
                            dialogWithData.dismiss()
                        }
                    }
                }

                if (type == "name") {
                    val restaurant = dialogWithData.restaurant
                    val dialogEdit = DialogEditData(restaurant.name)

                    dialogEdit.show(requireActivity().supportFragmentManager, dialogWithData.tag)
                    dialogEdit.listener = object : ListenerSubmitData {
                        override fun onSubmitData(submitData: String) {
                            item.name = submitData
                            viewModel.update(item)

                            val dialog = DialogWithData(item)
                            dialog.show(requireActivity().supportFragmentManager, dialog.tag)

                            dialogEdit.dismiss()
                            dialogWithData.dismiss()
                        }
                    }
                }
            }
        }
        return true
    }


    private fun observeDataFromNetwork(map: GoogleMap?) {
        Timber.d(" 4 ")
        viewModel
            .restaurantData
            .observe(viewLifecycleOwner, { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        listReasturant = resource.data
                        Timber.d(" 5 ")
                        listReasturant?.map {
                            Timber.d(" 6 ")
                            map?.addMarker(
                                MarkerOptions()
                                    .position(
                                        LatLng(
                                            it.latitude.toDouble(),
                                            it.longitude.toDouble()
                                        )
                                    )
                                    .title(it.name)
                            )
                        }
                    }
                    Status.LOADING -> {
                        Timber.d(" 77 ")
                        showLoading()
                    }
                    Status.ERROR -> {
                        Timber.d(" 88 ")
                        hideLoading()
                    }
                }
            }
            )
    }

    private fun observeRestaurantData(map: GoogleMap?) {
        Timber.d(" 4 ")
        viewModel
            .getFromDB()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : SingleObserver<List<Restaurant>> {
                    override fun onSubscribe(d: Disposable) {
                    }
                    override fun onSuccess(it: List<Restaurant>) {
                        if (it.size < 100) {
                            observeDataFromNetwork(map)
                        } else {
                            listReasturant = it
                            listReasturant!!.map {
                                Timber.d(" 6 ")
                                map?.addMarker(
                                    MarkerOptions()
                                        .position(
                                            LatLng(
                                                it.latitude.toDouble(),
                                                it.longitude.toDouble()
                                            )
                                        )
                                        .title(it.name)
                                )
                            }
                        }
                    }
                    override fun onError(e: Throwable) {
                    }
                })
    }


    private fun showLoading() {
        binding?.progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding?.progressBar?.visibility = View.GONE
    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMapHomeBinding =
        FragmentMapHomeBinding.inflate(inflater, container, false)


}
