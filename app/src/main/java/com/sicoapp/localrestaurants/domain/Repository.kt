package com.sicoapp.localrestaurants.domain

import com.sicoapp.localrestaurants.data.local.database.DatabaseDataSource
import com.sicoapp.localrestaurants.data.remote.NetworkDataSource
import com.sicoapp.localrestaurants.data.remote.Restaurant
import com.sicoapp.localrestaurants.domain.mappers.DataMapper
import com.sicoapp.localrestaurants.domain.mappers.mapToRestaurantEntity
import com.sicoapp.localrestaurants.utils.toV3Observable
import io.reactivex.Flowable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

/**
 * @author ll4
 * @date 1/26/2021
 */
class Repository
@Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val databaseDataSource: DatabaseDataSource
) {


    /**
     * save from network in database
     */
    fun getRestaurantsFromNetAndSaveIntoDB(): Observable<List<Restaurant>> {
        return networkDataSource
            .fetchRestaurants()
            .toObservable().toV3Observable()
            .doOnNext {
                it?.let { list ->
                    list.mapToRestaurantEntity().map {
                        databaseDataSource.saveRestaurants(it)
                    }
                }
            }
    }

    fun getRestaurantsDB(): Flowable<List<Restaurant>> {
        return databaseDataSource.getRestaurant().map {
            DataMapper.mapEntitiesToDomain(it)
        }
    }

    fun update(restaurant: Restaurant) {
        databaseDataSource.updateRestaurants(DataMapper.mapRestaurantToRestaurantEntity(restaurant))
    }

    fun add(restaurant: Restaurant) {
        databaseDataSource.addRestaurant(DataMapper.mapRestaurantToRestaurantEntity(restaurant))
    }

}