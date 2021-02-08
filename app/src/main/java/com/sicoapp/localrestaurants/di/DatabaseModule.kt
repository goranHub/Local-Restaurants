package com.sicoapp.localrestaurants.di

import android.content.Context
import androidx.room.Room
import com.sicoapp.localrestaurants.data.local.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

/**
 * @author ll4
 * @date 1/26/2021
 */
@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        Database::class.java,
        "restaurants_db"
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: Database) = db.movieDao()
}
