package com.sicoapp.localrestaurants.ui.photo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sicoapp.localrestaurants.BR
import kotlin.properties.Delegates

/**
 * @author ll4
 * @date 12/10/2020
 */
class BindMyProfile() : BaseObservable() {

    @get:Bindable
    var image  :String? by Delegates.observable("TEST image") { _, _, _ -> notifyPropertyChanged(BR.image) }


}