package com.sicoapp.localrestaurants.ui.map.bottom_sheet

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
    fun loadBmp(view: ImageView, bitmap: Bitmap?) {
        if (bitmap != null){
            view.setImageBitmap(bitmap)
        }
    }
}