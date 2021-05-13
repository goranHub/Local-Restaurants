package com.sicoapp.localrestaurants.ui.map

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
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
import com.sicoapp.localrestaurants.MainActivity
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import com.sicoapp.localrestaurants.domain.Restraurant
import com.sicoapp.localrestaurants.ui.BaseFR
import com.sicoapp.localrestaurants.utils.CAMERA_PIC_REQUEST
import com.sicoapp.localrestaurants.utils.livedata.Status
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class MapFragmentHome :
    BaseFR<FragmentMapHomeBinding, BaseActivity>(),
    OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val viewModel: MapViewModel by viewModels()
    private lateinit var mMapView: MapView
    private var listRestaurant: List<Restraurant>? = null
    private lateinit var item: Restraurant
    private lateinit var dialogWithData: DialogWithData
    override var binding: FragmentMapHomeBinding? = null
    private var map: GoogleMap? = null
    lateinit var imageFile: File


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
        //observeRestaurantData(map)
        observeRestaurantLiveData(map)

        Timber.d(" 1 ")

        map?.uiSettings?.isZoomControlsEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true

        Timber.d(" 2 ")

        map?.setOnMarkerClickListener(this)
        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(LatLng(45.83758, 16.05111), 14f)
        )

        Timber.d("animateCamera")
    }


    override fun onMarkerClick(marker: Marker): Boolean {

        item = listRestaurant!!.first { it.name == marker.title }
        dialogWithData = DialogWithData()
        dialogWithData.restaurant = item

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
        try {
            imageFile = createImageFile()
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(callCameraIntent.resolveActivity(requireContext().packageManager) != null) {
                val authorities = requireContext().packageName + ".fileprovider"
                val imageUri = FileProvider.getUriForFile(requireContext(), authorities, imageFile)
                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(callCameraIntent, CAMERA_PIC_REQUEST)
            }
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Could not create file!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_PIC_REQUEST) {

            val photo = bundleOf("photo" to   imageFile.absolutePath)

            findNavController().navigate(R.id.action_nav_map_to_restaurantPhotoFragment, photo)
            dialogWithData.dismiss()
            (activity as MainActivity).fab.hide()

        }
    }


    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir != null) {
            if(!storageDir.exists()) storageDir.mkdirs()
        }
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        return imageFile
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

            listRestaurant = it

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
                                Timber.d(" after addMarker 7 ")
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
