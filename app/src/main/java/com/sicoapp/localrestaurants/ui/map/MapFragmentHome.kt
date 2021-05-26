package com.sicoapp.localrestaurants.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.MainActivity
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.local.storage.SdStoragePhoto
import com.sicoapp.localrestaurants.data.remote.Restaurant
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import com.sicoapp.localrestaurants.domain.Repository
import com.sicoapp.localrestaurants.ui.BaseFR
import com.sicoapp.localrestaurants.ui.map.add.AddNewRestaurantDialog
import com.sicoapp.localrestaurants.ui.map.bottom_sheet.BottomSheetDialog
import com.sicoapp.localrestaurants.ui.map.place.LoadDataForCurrentPlace
import com.sicoapp.localrestaurants.ui.map.place.OpenPlaceDialog
import com.sicoapp.localrestaurants.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_diralog_with_data.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MapFragmentHome @Inject constructor(
    val restaurantDialog: RestaurantDialog,
    private val addRestaurantDialog: AddNewRestaurantDialog,
    private val bottomSheetDialog: BottomSheetDialog,
    val repository: Repository,
    var viewModel: MapViewModel? = null
) :
    BaseFR<FragmentMapHomeBinding, BaseActivity>(),
    OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private val layoutBinding get() = binding!!
    override var binding: FragmentMapHomeBinding? = null
    private lateinit var mMapView: MapView
    private lateinit var clickedRestaurant: Restaurant
    private lateinit var sdData: List<SdStoragePhoto>
    private var newRestaurant = Restaurant("", 0.0, 0.0, "", false)
    private var map: GoogleMap? = null
    private lateinit var imageFile: File
    private lateinit var placesClient: PlacesClient
    private var listRestaurant = mutableListOf<Restaurant>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(45.84107, 16.05076)
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            viewModel ?: ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        map?.uiSettings?.isZoomControlsEnabled = true

        checkHasInternetConnection()

        Places.initialize(
            requireActivity().applicationContext,
            getString(R.string.maps_api_key)
        )

        placesClient = Places.createClient(requireContext())

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        lifecycleScope.launch {
            sdData = StorageSdData.loadPhotosFromSdStorage(requireContext())
        }

        addRestaurantDialog.listener = callback

        fabClick()

        binding = setBinding(inflater, container)
        mMapView = layoutBinding.map
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)

        return layoutBinding.root
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.uiSettings?.isZoomControlsEnabled = true

        val locationButton =
            (mMapView.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
        (locationButton as ImageView).visibility = View.VISIBLE

        locationButton.setOnClickListener { getDeviceLocation() }

        map?.setOnMarkerClickListener(this)
        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(LatLng(45.84107, 16.05076), 14f)
        )

        getLocationPermission()
        getDeviceLocation()
        observeRestaurantLiveData(map)

    }

    private fun fabClick() {
        (activity as MainActivity).fab.setOnClickListener {
            bottomSheetDialog.sdData = sdData
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "test")
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        clickedRestaurant = listRestaurant.first { it.name == marker.title }

        restaurantDialog.restaurant = clickedRestaurant

        val bindPhotoToRestaurantDialog = sdData.firstOrNull {
            //check sdData name with marker name
            val sdDataName = it.name.subSequence(0, 4)
            val markerName =
                marker.title.subSequence(0, 4).toString().replace("^/^-^_".toRegex(), "")
                    .toUpperCase()
            sdDataName == markerName
        }

        restaurantDialog.mapToBind = bindPhotoToRestaurantDialog

        restaurantDialog.show(
            requireActivity().supportFragmentManager,
            restaurantDialog.tag
        )

        restaurantDialog.listener = object : RestaurantDialog.ListenerClicked {

            override fun onButtonLatitude() {
                val dialogEdit = modifyData(restaurantDialog.restaurant.latitude.toString())
                dialogEdit.listener = object : ListenerEditData {
                    override fun onSubmitData(latitude: String) {
                        clickedRestaurant.latitude = latitude.toDouble()
                        updateDBAndDismissDialog(dialogEdit)
                    }
                }
            }

            override fun onButtonLongitude() {
                val dialogEdit = modifyData(restaurantDialog.restaurant.longitude.toString())
                dialogEdit.listener = object : ListenerEditData {
                    override fun onSubmitData(longitude: String) {
                        clickedRestaurant.longitude = longitude.toDouble()
                        updateDBAndDismissDialog(dialogEdit)
                    }
                }
            }

            override fun onButtonAddress() {
                val dialogEdit = modifyData(restaurantDialog.restaurant.address)
                dialogEdit.listener = object : ListenerEditData {
                    override fun onSubmitData(address: String) {
                        clickedRestaurant.address = address
                        updateDBAndDismissDialog(dialogEdit)
                    }
                }
            }

            override fun onButtonName() {
                val dialogEdit = modifyData(restaurantDialog.restaurant.name)

                dialogEdit.listener = object : ListenerEditData {
                    override fun onSubmitData(submitName: String) {
                        listRestaurant.filter { it.name == restaurantDialog.restaurant.name }
                            .forEach { it.name = restaurantDialog.restaurant.name }
                        updateDBAndDismissDialog(dialogEdit)
                    }
                }
            }

            override fun onImageView() {
                imageChooserCamera()
            }
        }
        return true
    }

    private fun modifyData(type: String): DialogEditData {
        val dialogEdit = DialogEditData(type)
        dialogEdit.show(requireActivity().supportFragmentManager, restaurantDialog.tag)
        return dialogEdit
    }

    private fun updateDBAndDismissDialog(dialogEdit: DialogEditData) {
        repository.update(this@MapFragmentHome.clickedRestaurant)
        dialogEdit.dismiss()
        restaurantDialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_PIC_REQUEST) {
            clickedRestaurant.photoTaken = true
            repository.update(clickedRestaurant)

            val photo = bundleOf("photo" to imageFile.absolutePath)
            findNavController().navigate(R.id.action_nav_map_to_restaurantPhotoFragment, photo)
            restaurantDialog.dismiss()
            (activity as MainActivity).fab.hide()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun imageChooserCamera() {
        try {
            imageFile = CreateImgFile.createTmpFile(requireContext(), clickedRestaurant.name)
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCameraIntent.resolveActivity(requireContext().packageManager) != null) {
                val authorities = requireContext().packageName + ".fileprovider"
                val imageUri = FileProvider.getUriForFile(requireContext(), authorities, imageFile)
                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(callCameraIntent, CAMERA_PIC_REQUEST)
            }
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Could not create file!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeRestaurantLiveData(map: GoogleMap?) {
        viewModel?.restaurantData?.observe(viewLifecycleOwner, { resource ->

            when (resource.status) {
                Status.SUCCESS -> {
                    listRestaurant = resource.data as MutableList<Restaurant>

                    listRestaurant.map {
                        map?.addMarker(
                            MarkerOptions().position(
                                LatLng(it.latitude, it.longitude)
                            ).title(it.name)
                        )
                    }
                }
                Status.ERROR -> {
                    showLoading()
                }
                Status.LOADING -> {
                    hideLoading()
                }
            }
        })
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Timber.d("Current location is null")
                        map?.animateCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Timber.e("Exception: %s", e.message)
        }
    }


    private val callback = object : AddNewRestaurantDialog.ListenerClicked {
        override fun onNewRestaurant(name: String, address: String) {
            val newMarker = map?.addMarker(
                MarkerOptions()
                    .title(name)
                    .position(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude))
            )
            newRestaurant = Restaurant(
                address,
                lastKnownLocation!!.latitude,
                lastKnownLocation!!.longitude,
                newMarker!!.title,
                false
            )
            //add new restaurant by AddNewRestaurantDialog
            listRestaurant.add(newRestaurant)
            repository.add(newRestaurant)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add) {
            addRestaurantDialog.show(
                requireActivity().supportFragmentManager,
                addRestaurantDialog.tag
            )
        }
        if (item.itemId == R.id.get_place) {
            if (locationPermissionGranted) {
                LoadDataForCurrentPlace.getDataForCurrentPlace(
                    map, placesClient,
                    requireContext(), sdData, listRestaurant
                )

                //upload sdData variable for markerClick and bottomSheet
                lifecycleScope.launch {
                    sdData = StorageSdData.loadPhotosFromSdStorage(requireContext())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkHasInternetConnection(): Boolean {
        val mainActivity = activity as MainActivity
        mainActivity.checkInternetConnection()
        return hasInternetConnection(mainActivity)
    }

    private fun showLoading() {
        layoutBinding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        layoutBinding.progressBar.visibility = View.GONE
    }

    override fun onResume() {
        showLoading()
        (activity as MainActivity).fab.show()
        mMapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onLowMemory() {
        mMapView.onLowMemory()
        super.onLowMemory()
    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMapHomeBinding =
        FragmentMapHomeBinding.inflate(inflater, container, false)
}
