package com.sicoapp.localrestaurants.data.di

import android.content.Context
import com.sicoapp.localrestaurants.BaseHiltActivity
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

}