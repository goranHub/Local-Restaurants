package com.sicoapp.localrestaurants.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 * @author ll4
 * @date 3/24/2021
 */
object ImageBindingAdapter {
    @JvmStatic
    @BindingAdapter("loadImageDetails")
    fun loadImageDetails(view: ImageView, profileImage: String?) {
        Glide.with(view.context)
            .load(profileImage)
            .fitCenter()
            .into(view)
    }
}
