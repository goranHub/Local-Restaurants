package com.sicoapp.localrestaurants.ui.gallery

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class MapViewModel  @ViewModelInject constructor(
    @ApplicationContext application: Context,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {


}