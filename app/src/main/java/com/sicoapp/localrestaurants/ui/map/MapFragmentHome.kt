package com.sicoapp.localrestaurants.ui.map

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.local.Restaurant
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import com.sicoapp.localrestaurants.databinding.FragmentRestaurantPhotoBinding
import com.sicoapp.localrestaurants.ui.BaseFR
import com.sicoapp.localrestaurants.ui.photo.BindMyProfile
import com.sicoapp.localrestaurants.utils.*
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
    private var listRestaurant: List<Restaurant>? = null
    private lateinit var item: Restaurant
    override var binding: FragmentMapHomeBinding? = null
    private var map: GoogleMap? = null
    var selectedImageUri: Uri? = null
    private lateinit var bindingRes: FragmentRestaurantPhotoBinding

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
        observeRestaurantData(map)

        Timber.d(" 1 ")

        map?.uiSettings?.isZoomControlsEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true

        Timber.d(" 2 ")

        map?.setOnMarkerClickListener(this)
        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(LatLng(45.83758, 16.05111), 14f)
        )

        Timber.d("3")
    }


    override fun onMarkerClick(marker: Marker): Boolean {

        item = listRestaurant!!.first { it.name == marker.title }

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
                            Timber.d(" update latitude")
                            viewModel.update(item)
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
                            Timber.d(" update longitude  ")
                            viewModel.update(item)
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
                            Timber.d(" update address")
                            viewModel.update(item)
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
                            Timber.d(" update name ")
                            viewModel.update(item)
                            dialogEdit.dismiss()
                            dialogWithData.dismiss()
                        }
                    }
                }

                if (type == "photo") {
                    Timber.d(" photo")
                    imageChooser()
                }

            }
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PIC_REQUEST) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageChooser()
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    context,
                    "you denied the permission",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun imageChooser() {
        val cameraIntent  = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        this.startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST)
    }


    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        if (requestCode ==  CAMERA_PIC_REQUEST
            && intent!!.data != null
        ) {
            selectedImageUri = intent.data
            BindMyProfile().image = selectedImageUri.toString()
            findNavController().navigate(R.id.action_nav_map_to_restaurantPhotoFragment)
        }
    }


    private fun observeDataFromNetwork(map: GoogleMap?) {
        Timber.d(" observeDataFromNetwork 4 ")
        viewModel
            .restaurantData
            .observe(viewLifecycleOwner, { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        listRestaurant = resource.data
                        Timber.d(" observeDataFromNetwork 5 ")
                        listRestaurant?.map {
                            Timber.d(" observeDataFromNetwork 6 ")
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
                        Timber.d(" observeDataFromNetwork 7 ")
                        showLoading()
                    }
                    Status.ERROR -> {
                        Timber.d(" observeDataFromNetwork 8 ")
                        hideLoading()
                    }
                }
            }
            )
    }

    private fun observeRestaurantData(map: GoogleMap?) {
        Timber.d(" observeRestaurantData 4 ")
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
                            Timber.d(" observeDataFromNetwork init ")

                            it.map {
                                viewModel.saveRestaurants(it)
                            }
                            Timber.d(" saveRestaurants in DB 6 ")

                        } else {
                            listRestaurant = it
                            listRestaurant!!.map {
                                Timber.d(" observeRestaurantData FromDB 6 ")
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
