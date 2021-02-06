package com.sicoapp.localrestaurants.ui.map

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sicoapp.localrestaurants.data.remote.RestaurantServis
import com.sicoapp.localrestaurants.utils.enqueueR
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber

class MapViewModel  @ViewModelInject constructor(
    @ApplicationContext application: Context,
    private val restaurantServis: RestaurantServis
) : ViewModel() {

    var showMapCallback: ShowMapCallback? = null

    val name = MutableLiveData<String>()

    fun sendName(text: String) {
        name.value = text
    }

    init {
        load()
    }

    private fun load() {
        val currentCall = restaurantServis.get()

        currentCall.enqueueR {
            this.onResponse = {
                Timber.d("onResponse")
                showMapCallback?.onResponse(it.body()!!)

                it.body()?.map {
                    Timber.d(it.name)
                }
            }
            this.onFailure = {
                Timber.d("onFailure")
            }
        }
    }
}