package com.sicoapp.localrestaurants.di

import android.content.Context
import android.content.SharedPreferences
import com.sicoapp.localrestaurants.BaseHiltActivity
import com.sicoapp.localrestaurants.utils.KEY_NAME
import com.sicoapp.localrestaurants.utils.KEY_RATING
import com.sicoapp.localrestaurants.utils.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): BaseHiltActivity {
        return app as BaseHiltActivity
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getInt(KEY_RATING, 1)

}