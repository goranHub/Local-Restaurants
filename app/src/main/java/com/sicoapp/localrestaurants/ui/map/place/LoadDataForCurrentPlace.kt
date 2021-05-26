package com.sicoapp.localrestaurants.ui.map.place

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.local.storage.SdStoragePhoto
import com.sicoapp.localrestaurants.data.remote.Restaurant
import com.sicoapp.localrestaurants.utils.M_MAX_ENTRIES
import timber.log.Timber

/**
 * @author lllhr
 * @date 5/26/2021
 */
object LoadDataForCurrentPlace {

    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)
    private var likelyPlacePhoto: Array<MutableList<PhotoMetadata>?> = arrayOfNulls(0)



    @SuppressLint("MissingPermission")
    fun getDataForCurrentPlace(
        map: GoogleMap?, placesClient: PlacesClient,
        ctx: Context, sdData: List<SdStoragePhoto>,
        listRestaurant: MutableList<Restaurant>
    ) {

        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.PHOTO_METADATAS
        )

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
                likelyPlacePhoto = arrayOfNulls(count)
                for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
                    // Build a provideMutableListOfRestaurants of likely places to show the user.
                    likelyPlaceNames[i] = placeLikelihood.place.name
                    likelyPlaceAddresses[i] = placeLikelihood.place.address
                    likelyPlaceAttributions[i] = placeLikelihood.place.attributions
                    likelyPlaceLatLngs[i] = placeLikelihood.place.latLng
                    likelyPlacePhoto[i] = placeLikelihood.place.photoMetadatas
                    i++
                    if (i > count - 1) {
                        break
                    }
                }

                OpenPlaceDialog.openPlacesDialog(
                    map, ctx, likelyPlaceNames, likelyPlaceAddresses,
                    likelyPlaceLatLngs, likelyPlacePhoto, placesClient,
                    sdData, listRestaurant
                )

            } else {
                Timber.e("task exception")
            }
        }
    }
}
