package com.sicoapp.localrestaurants

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * @author ll4
 * @date 1/26/2021
 */

@HiltAndroidApp
class BaseHiltActivity : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}