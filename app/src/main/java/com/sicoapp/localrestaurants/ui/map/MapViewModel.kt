package com.sicoapp.localrestaurants.ui.map

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class MapViewModel  @ViewModelInject constructor(
    @ApplicationContext application: Context,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {


}