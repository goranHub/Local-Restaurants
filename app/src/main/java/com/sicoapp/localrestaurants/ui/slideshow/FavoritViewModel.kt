package com.sicoapp.localrestaurants.ui.slideshow

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class FavoritViewModel @ViewModelInject constructor(
    @ApplicationContext application: Context,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text

/*    private fun writeFavoritRestaurantDataToSharedPref(): Boolean {
        val name = ""
        val rating = 1
        if(name.isEmpty() || rating.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putInt(KEY_RATING, rating)
            .apply()
        val toolbarText = "Let's go, $name!"
        requireActivity().actionBar.title = "toolbarText"
        return true
    }*/

}