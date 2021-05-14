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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_diralog_with_data.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class MapFragmentHome :
    BaseFR<FragmentMapHomeBinding, BaseActivity>(),
    OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    override var binding: FragmentMapHomeBinding? = null
    private lateinit var mMapView: MapView
    private lateinit var restraurant: Restraurant
    private lateinit var dialogWithData: DialogWithData
    private lateinit var imageFile: File
    private var map: GoogleMap? = null
    private val viewModel: MapViewModel by viewModels()
    private var listRestaurant: List<Restraurant>? = null

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

        restraurant = listRestaurant!!.first { it.name == marker.title }
        dialogWithData = DialogWithData()
        dialogWithData.restaurant = restraurant

        dialogWithData.show(requireActivity().supportFragmentManager, dialogWithData.tag)

        dialogWithData.listener = object : DialogWithData.ListenerClicked {

            override fun onButton(type: String) {

                if (type == "latitude") {
                    val restaurant = dialogWithData.restaurant
                    val dialogEdit = DialogEditData(restaurant.latitude)

                    dialogEdit.show(requireActivity().supportFragmentManager, dialogWithData.tag)
                    dialogEdit.listener = object : ListenerSubmitData {
                        override fun onSubmitData(submitData: String) {
                            restraurant.latitude = submitData
                            Timber.d(" update latitude")
                            viewModel.update(restraurant)
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
                            restraurant.longitude = submitData
                            Timber.d(" update longitude  ")
                            viewModel.update(restraurant)
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
                            restraurant.address = submitData
                            Timber.d(" update address")
                            viewModel.update(restraurant)
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
                            restraurant.name = submitData
                            Timber.d(" update name ")
                            viewModel.update(restraurant)
                            dialogEdit.dismiss()
                            dialogWithData.dismiss()
                        }
                    }
                }

                if (type == "photo") {
                    Timber.d(" photo")
                    if(restraurant.photoTaken){
                        dialogWithData.bt_photo.visibility = View.GONE
                    }else{
                        imageChooser()
                    }
                }
            }
        }
        return true
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

            restraurant.photoTaken = true
            viewModel.update(restraurant)
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
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }


    private fun observeRestaurantLiveData(map: GoogleMap?) {
        Timber.d(" observeRestaurantData 4 ")
        viewModel.restraurantsFormDBLiveData.observe(viewLifecycleOwner, {

            listRestaurant = it

            it.map {
                map?.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            it.latitude.toDouble(),
                            it.longitude.toDouble()
                        )
                    ).title(it.name)
                )
            }
        })
    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMapHomeBinding =
        FragmentMapHomeBinding.inflate(inflater, container, false)
}
