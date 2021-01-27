package com.sicoapp.localrestaurants.ui.home

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sicoapp.localrestaurants.data.remote.RestaurantServis
import com.sicoapp.localrestaurants.utils.enqueueR
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber


class HomeViewModel @ViewModelInject constructor(
    @ApplicationContext application: Context,
    private val restaurantServis: RestaurantServis
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    init {
        load()
    }

    private fun load() {
        val currentCall = restaurantServis.get()

        currentCall.enqueueR {
            this.onResponse = {
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





