package com.sicoapp.localrestaurants.ui.map

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding4.view.clicks
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import com.sicoapp.localrestaurants.databinding.FragmentRestaurantPhotoBinding
import com.sicoapp.localrestaurants.domain.Restraurant
import com.sicoapp.localrestaurants.ui.BaseFR
import com.sicoapp.localrestaurants.utils.CAMERA_PIC_REQUEST
import com.sicoapp.localrestaurants.utils.livedata.Status
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_diralog_with_data.*
import timber.log.Timber


@AndroidEntryPoint
class MapFragmentHome :
    BaseFR<FragmentMapHomeBinding, BaseActivity>(),
    OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val viewModel: MapViewModel by viewModels()
    private lateinit var mMapView: MapView
    private var listRestaurant: List<Restraurant>? = null
    private lateinit var item: Restraurant
    override var binding: FragmentMapHomeBinding? = null
    private var map: GoogleMap? = null
    var selectedImageUri: Uri? = null
    private lateinit var bindingRes: FragmentRestaurantPhotoBinding
    private val disposables = CompositeDisposable()


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
        //observeRestaurantLiveData(map)

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


                bt_address.clicks().subscribe {
                    viewModel.addClicked()
                }.addTo(disposables)

                viewModel.showDialogWithData.observe(viewLifecycleOwner, Observer {
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
                })

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
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        this.startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_PIC_REQUEST
            && data != null
        ) {
            selectedImageUri = data.data

            val photo = bundleOf("photo" to selectedImageUri.toString())
            findNavController().navigate(R.id.action_nav_map_to_restaurantPhotoFragment, photo)
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
            })
    }

    private fun observeRestaurantLiveData(map: GoogleMap?) {
        Timber.d(" observeRestaurantData 4 ")
        viewModel.restraurantsFormDBLiveData.observe(viewLifecycleOwner, {

            it.map {
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
        })
    }

    private fun observeRestaurantData(map: GoogleMap?) {
        Timber.d(" observeRestaurantData 4 ")
        viewModel
            .getFromDB().doOnSubscribe {
                Timber.d(" doOnSubscribe getFromDB")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(

                object : SingleObserver<List<Restraurant>> {
                    override fun onSubscribe(d: Disposable) {
                        Timber.d(" Disposable ")
                    }

                    override fun onSuccess(it: List<Restraurant>) {
                        if (it.size < 100) {
                            observeDataFromNetwork(map)
                            Timber.d(" observeDataFromNetwork init ")

                            it.map {
                                viewModel.saveRestaurants(it)
                            }

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
                        Timber.d(" Throwable ")
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
