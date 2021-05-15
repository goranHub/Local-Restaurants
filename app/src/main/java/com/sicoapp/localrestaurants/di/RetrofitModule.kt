package com.sicoapp.localrestaurants.di

import com.sicoapp.localrestaurants.data.remote.RestaurantServis
import com.sicoapp.localrestaurants.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * @author ll4
 * @date 1/26/2021
 */
@Module
@InstallIn(ApplicationComponent::class)
object RetrofitModule {

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC
            return OkHttpClient.Builder().addInterceptor(logger).build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): RestaurantServis = retrofit.create(RestaurantServis::class.java)
}

