package com.sicoapp.localrestaurants.utils

import android.graphics.Bitmap
import com.sicoapp.localrestaurants.data.local.storage.SdStoragePhoto
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.Observable

/**
 * @author ll4
 * @date 5/7/2021
 */

    fun <T> Observable<T>.toV3Observable(): io.reactivex.rxjava3.core.Observable<T> {
        return RxJavaBridge.toV3Observable(this)
    }


    fun SdStoragePhoto.bmpIsNotNull() : Boolean{
        return bmp != null
    }