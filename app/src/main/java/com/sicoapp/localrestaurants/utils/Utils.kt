package com.sicoapp.localrestaurants.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.sicoapp.localrestaurants.data.local.storage.SdStoragePhoto
import com.sicoapp.localrestaurants.data.remote.Restraurant
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author ll4
 * @date 5/7/2021
 */

fun <T> Observable<T>.toV3Observable(): io.reactivex.rxjava3.core.Observable<T> {
    return RxJavaBridge.toV3Observable(this)
}


fun SdStoragePhoto.bmpIsNotNull(): Boolean {
    return bmp != null
}

object StorageSdData {
    suspend fun loadPhotosFromSdStorage(context: Context): List<SdStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val storageDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.listFiles()
            storageDir?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                SdStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }
}

object CreateImgFile{
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun create(context: Context, clickedRestaurant : Restraurant) : File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = clickedRestaurant.name.toUpperCase() + "_" + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir != null) {
            if (!storageDir.exists()) storageDir.mkdirs()
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }
}



