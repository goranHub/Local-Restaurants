package com.sicoapp.localrestaurants.ui.all

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

/**
 * @author lllhr
 * @date 5/21/2021
 */
object BitmapBindingAdapter {
    @JvmStatic
    @BindingAdapter("loadBmp")
    fun loadBmp(view: ImageView, bitmap: Bitmap) {
        view.setImageBitmap(bitmap)
    }
}