package com.sicoapp.localrestaurants.ui.map.place

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.local.storage.SdStoragePhoto
import com.sicoapp.localrestaurants.data.remote.Restaurant
import com.sicoapp.localrestaurants.domain.Repository
import com.sicoapp.localrestaurants.utils.DEFAULT_ZOOM
import com.sicoapp.localrestaurants.utils.StorageSdData
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * @author lllhr
 * @date 5/26/2021
 */
object OpenPlaceDialog {

    lateinit var  listener : DialogInterface.OnClickListener
    private var newRestaurant = Restaurant("", 0.0, 0.0, "", false)

    fun openPlacesDialog(
        map: GoogleMap?, ctx: Context,
        likelyPlaceNames: Array<String?>,
        likelyPlaceAddresses: Array<String?>,
        likelyPlaceLatLngs: Array<LatLng?>,
        likelyPlacePhoto: Array<MutableList<PhotoMetadata>?>,
        placesClient: PlacesClient,
        sdData: List<SdStoragePhoto>,
        listRestaurant : MutableList<Restaurant>
    ) {

        listener = DialogInterface.OnClickListener { _, which ->

            val newMarker = map?.addMarker(
                MarkerOptions()
                    .title(likelyPlaceNames[which]?.replace("^/^-^_".toRegex(), "")?.toUpperCase())
                    .position(likelyPlaceLatLngs[which]!!)
            )

            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        likelyPlaceLatLngs[which]!!.latitude,
                        likelyPlaceLatLngs[which]!!.longitude
                    ), DEFAULT_ZOOM.toFloat()
                )
            )

            if (likelyPlacePhoto[which] != null) {
                val photoMetadata = likelyPlacePhoto[which]?.get(0)
                val photoRequest = FetchPhotoRequest.builder(photoMetadata!!)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build()

                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->

                        val bitmap = fetchPhotoResponse.bitmap

                        if (!(StorageSdData.isSaved(likelyPlaceNames[which]!!, sdData))) {
                            StorageSdData.savePhotoToInternalStorage(
                                ctx,
                                likelyPlaceNames[which]!!,
                                bitmap
                            )

                            newRestaurant = Restaurant(
                                likelyPlaceAddresses[which].toString(),
                                newMarker!!.position.latitude,
                                newMarker.position.longitude,
                                newMarker.title,
                                true
                            )

                        /*    //upload sdData variable for markerClick and bottomSheet
                            lifecycleScope.launch {
                                sdData = StorageSdData.loadPhotosFromSdStorage(ctx)
                            }*/

                            listRestaurant.add(newRestaurant)

                        }

                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            Timber.e("Place not found: %s", exception.message)
                            //val statusCode = exception.statusCode
                            Timber.d("Handle error with given status code.")
                        }
                    }
            } else {
                newRestaurant = Restaurant(
                    likelyPlaceAddresses[which].toString(),
                    newMarker!!.position.latitude,
                    newMarker.position.longitude,
                    newMarker.title,
                    false
                )

                listRestaurant.add(newRestaurant)

            }
        }

        AlertDialog.Builder(ctx)
            .setTitle(R.string.pick_place)
            .setItems(likelyPlaceNames, listener)
            .show()

    }
}