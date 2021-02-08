package com.sicoapp.localrestaurants.di

import com.sicoapp.localrestaurants.data.local.DatabaseDataSource
import com.sicoapp.localrestaurants.data.remote.NetworkDataSource
import com.sicoapp.localrestaurants.domain.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @author ll4
 * @date 1/26/2021
 */
@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {


    @Singleton
    @Provides
    fun provideMovieRepository(
        networkDataSource: NetworkDataSource,
        databaseDataSource: DatabaseDataSource
    ): Repository {
        return Repository(
            networkDataSource = networkDataSource,
            databaseDataSource = databaseDataSource
        )
    }
}



