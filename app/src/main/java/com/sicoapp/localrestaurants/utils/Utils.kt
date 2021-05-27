package com.sicoapp.localrestaurants.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.sicoapp.localrestaurants.data.local.storage.SdStoragePhoto
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
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
                //Save into SdStoragePhoto
                SdStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }

    fun savePhotoToInternalStorage(context: Context, filename: String, bmp: Bitmap): Boolean {

        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
        val fileName = filename.replace("^/^-^_".toRegex(), "")
        val tmpFile = CreateImgFile.createTmpFile(context, fileName).absolutePath

        return try {
            val out = FileOutputStream(tmpFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 95, out);
            out.flush();
            out.close();
            true
        } catch (e: IOException) {
            e.printStackTrace();
            false
        }
    }

    fun isSaved(likelyPlacePhotoName: String, sdData: List<SdStoragePhoto>): Boolean {
        var isSaved = false
        val photoName =
            likelyPlacePhotoName.replace("^/^-^_".toRegex(), "").toUpperCase().subSequence(0, 4)
                .toString()

       val sdDataFilter = sdData.filter {
            val savedName = it.name.subSequence(0, 4).toString()
            savedName == photoName
        }

        return sdDataFilter.isNotEmpty()
    }

}


object CreateImgFile {
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createTmpFile(context: Context, restaurantName: String): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = restaurantName.toUpperCase() + "_" + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir != null) {
            if (!storageDir.exists()) storageDir.mkdirs()
        }
        val name = imageFileName.replace("^/^-^_".toRegex(), "")
        return File.createTempFile(name, ".jpg", storageDir)
    }
}



