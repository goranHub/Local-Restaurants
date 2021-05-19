package com.sicoapp.localrestaurants.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.MainActivity
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.databinding.FragmentMapHomeBinding
import com.sicoapp.localrestaurants.domain.SdStoragePhoto
import com.sicoapp.localrestaurants.domain.Restraurant
import com.sicoapp.localrestaurants.ui.BaseFR
import com.sicoapp.localrestaurants.ui.add.AddRestaurantDialog
import com.sicoapp.localrestaurants.ui.add.BottomSheetDialog
import com.sicoapp.localrestaurants.utils.CAMERA_PIC_REQUEST
import com.sicoapp.localrestaurants.utils.DEFAULT_ZOOM
import com.sicoapp.localrestaurants.utils.M_MAX_ENTRIES
import com.sicoapp.localrestaurants.utils.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_diralog_with_data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private lateinit var restaurant: Restraurant
    private lateinit var dialogWithData: DialogWithData
    private var newRestaurant = Restraurant("",0.0,0.0,"",false)
    private val addRestaurantDialog by lazy { AddRestaurantDialog() }
    private lateinit var imageFile: File
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var map: GoogleMap? = null
    private val viewModel: MapViewModel by viewModels()
    private lateinit var placesClient: PlacesClient
    private var listRestaurant = mutableListOf<Restraurant>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(45.84107, 16.05076)
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null
    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.let {
            Places.initialize(
                it.applicationContext,
                getString(R.string.google_maps_api_key)
            )
        }

        loadPhotosFromSdStorageIntoBottomSheetDialog()

        addRestaurantDialog.listener = callback

        placesClient = Places.createClient(requireContext())

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        fabClick()

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
                .newLatLngZoom(LatLng(45.84107, 16.05076), 14f)
        )

        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()

        Timber.d("animateCamera")
    }

    private fun fabClick() {
        (activity as MainActivity).fab.setOnClickListener {
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "test")
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        listRestaurant.add(newRestaurant)

        restaurant = listRestaurant.first { it.name == marker.title }
        dialogWithData = DialogWithData()
        dialogWithData.restaurant = restaurant

        dialogWithData.show(requireActivity().supportFragmentManager, dialogWithData.tag)

        dialogWithData.listener = object : DialogWithData.ListenerClicked {

            override fun onButton(type: String) {

                if (type == "latitude") {
                    val restaurant = dialogWithData.restaurant
                    val dialogEdit = DialogEditData(restaurant.latitude.toString())

                    dialogEdit.show(requireActivity().supportFragmentManager, dialogWithData.tag)
                    dialogEdit.listener = object : ListenerSubmitData {
                        override fun onSubmitData(submitData: String) {
                            this@MapFragmentHome.restaurant.latitude = submitData.toDouble()
                            Timber.d(" update latitude")
                            viewModel.update(this@MapFragmentHome.restaurant)
                            dialogEdit.dismiss()
                            dialogWithData.dismiss()
                        }
                    }
                }

                if (type == "longitude") {
                    val restaurant = dialogWithData.restaurant
                    val dialogEdit = DialogEditData(restaurant.longitude.toString())

                    dialogEdit.show(requireActivity().supportFragmentManager, dialogWithData.tag)
                    dialogEdit.listener = object : ListenerSubmitData {
                        override fun onSubmitData(submitData: String) {
                            this@MapFragmentHome.restaurant.longitude = submitData.toDouble()
                            Timber.d(" update longitude  ")
                            viewModel.update(this@MapFragmentHome.restaurant)
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
                            this@MapFragmentHome.restaurant.address = submitData
                            Timber.d(" update address")
                            viewModel.update(this@MapFragmentHome.restaurant)
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
                            this@MapFragmentHome.restaurant.name = submitData
                            Timber.d(" update name ")
                            viewModel.update(this@MapFragmentHome.restaurant)
                            dialogEdit.dismiss()
                            dialogWithData.dismiss()
                        }
                    }
                }

                if (type == "photo") {
                    Timber.d(" photo")
                    if (restaurant.photoTaken) {
                        dialogWithData.bt_photo.visibility = View.GONE
                    } else {
                        imageChooser()
                    }
                }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_PIC_REQUEST) {

            restaurant.photoTaken = true
            viewModel.update(restaurant)
            val photo = bundleOf("photo" to imageFile.absolutePath)

            findNavController().navigate(R.id.action_nav_map_to_restaurantPhotoFragment, photo)
            dialogWithData.dismiss()
           // (activity as MainActivity).fab.hide()

        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun imageChooser() {
        try {
            imageFile = createImageFile()
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

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = restaurant.name.toUpperCase()  + "_" + timeStamp + "_"
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir != null) {
            if (!storageDir.exists()) storageDir.mkdirs()
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun observeRestaurantLiveData(map: GoogleMap?) {

        Timber.d(" obseerveRestaurantData 4 ")
        viewModel.restaurantsFormDBLiveData.observe(viewLifecycleOwner, { list ->

            if (list.isEmpty()) {
                viewModel.getRestaurantsFromNetAndSaveIntoDB()
            } else {
                listRestaurant = list as MutableList<Restraurant>

                list.map {
                    map?.addMarker(
                        MarkerOptions().position(
                            LatLng(it.latitude, it.longitude)
                        ).title(it.name)
                    )
                }
            }
        })
    }


    private fun getLocationPermission() {

        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED) {
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

                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Timber.e("SecurityException")
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
                            map?.moveCamera(
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
                        map?.moveCamera(
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


    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        if (map == null) {
            return
        }
        if (locationPermissionGranted) {

            val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            val placeResult = placesClient.findCurrentPlace(request)
            placeResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {

                    val likelyPlaces = task.result
                    val count =
                        if (likelyPlaces != null && likelyPlaces.placeLikelihoods.size < M_MAX_ENTRIES) {
                            likelyPlaces.placeLikelihoods.size
                        } else {
                            M_MAX_ENTRIES
                        }

                    var i = 0
                    likelyPlaceNames = arrayOfNulls(count)
                    likelyPlaceAddresses = arrayOfNulls(count)
                    likelyPlaceAttributions = arrayOfNulls<List<*>?>(count)
                    likelyPlaceLatLngs = arrayOfNulls(count)
                    for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
                        // Build a list of likely places to show the user.
                        likelyPlaceNames[i] = placeLikelihood.place.name
                        likelyPlaceAddresses[i] = placeLikelihood.place.address
                        likelyPlaceAttributions[i] = placeLikelihood.place.attributions
                        likelyPlaceLatLngs[i] = placeLikelihood.place.latLng
                        i++
                        if (i > count - 1) {
                            break
                        }
                    }
                    openPlacesDialog()
                } else {
                    Timber.e("task exception")
                }
            }
        } else {
            Timber.d("Not grant location permission.")
            map?.addMarker(
                MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(defaultLocation)
                    .snippet(getString(R.string.default_info_snippet))
            )
            getLocationPermission()
        }
    }


    private val callback = object : AddRestaurantDialog.ListenerClicked {
        override fun onName(name: String) {

            val newMarker = map?.addMarker(
                MarkerOptions()
                    .title(name)
                    .position(
                        LatLng(
                            lastKnownLocation!!.latitude,
                            lastKnownLocation!!.longitude
                        )
                    )
            )

            newRestaurant = Restraurant(
                "",
                lastKnownLocation!!.latitude,
                lastKnownLocation!!.longitude,
                newMarker!!.title,
                false
            )
            Timber.d(name)
        }

        override fun onAddress(address: String) {
            newRestaurant.address = address
            Timber.d(address)
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
            showCurrentPlace()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openPlacesDialog() {

        val listener = DialogInterface.OnClickListener { _, which ->
            val markerLatLng = likelyPlaceLatLngs[which]
            var markerSnippet = likelyPlaceAddresses[which]
            if (likelyPlaceAttributions[which] != null) {
                markerSnippet = """
                    $markerSnippet
                    ${likelyPlaceAttributions[which]}
                    """.trimIndent()
            }

            val newMarker = map?.addMarker(
                MarkerOptions()
                    .title(likelyPlaceNames[which])
                    .position(markerLatLng!!)
                    .snippet(markerSnippet)
            )


            newRestaurant = Restraurant(
                likelyPlaceAddresses[which].toString(),
                newMarker!!.position.latitude,
                newMarker.position.longitude,
                newMarker.title,
                false
            )
            listRestaurant.add(newRestaurant)
            viewModel.update(newRestaurant)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.pick_place)
            .setItems(likelyPlaceNames, listener)
            .show()
    }

    private suspend fun loadPhotosFromSdStorage(): List<SdStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?.listFiles()
            storageDir?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                SdStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }


    private fun loadPhotosFromSdStorageIntoBottomSheetDialog() {
        lifecycleScope.launch {
            val photos = loadPhotosFromSdStorage()
            bottomSheetDialog = BottomSheetDialog(photos)
        }
    }


    override fun onResume() {
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
