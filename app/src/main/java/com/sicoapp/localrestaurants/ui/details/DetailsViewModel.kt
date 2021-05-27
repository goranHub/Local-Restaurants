package com.sicoapp.localrestaurants.ui.details

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sicoapp.localrestaurants.data.remote.RestaurantServis
import dagger.hilt.android.qualifiers.ApplicationContext


class DetailsViewModel @ViewModelInject constructor(
    @ApplicationContext application: Context,
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

}





