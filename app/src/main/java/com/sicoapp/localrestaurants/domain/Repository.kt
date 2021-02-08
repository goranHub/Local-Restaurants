package com.sicoapp.localrestaurants.domain

import com.sicoapp.localrestaurants.data.local.DatabaseDataSource
import com.sicoapp.localrestaurants.data.local.Restaurant
import com.sicoapp.localrestaurants.data.remote.NetworkDataSource
import com.sicoapp.localrestaurants.domain.mappers.mapToRestaurant
import io.reactivex.Observable

/**
 * @author ll4
 * @date 1/26/2021
 */
class Repository(
    private val networkDataSource: NetworkDataSource,
    private val databaseDataSource: DatabaseDataSource
){


    /**
     * save from network in database
     */
    fun fetchRestaurants(): Observable<List<Restaurant>> {
        return networkDataSource
            .fetchRestaurants()
            .toObservable()
            .map {
                it.map {
                    it.mapToRestaurant()
                }
            }
            .doOnNext {
                it?.let {list ->
                    list.forEach {
                        databaseDataSource.saveRestaurants(it)
                    }
                }
            }
    }
}