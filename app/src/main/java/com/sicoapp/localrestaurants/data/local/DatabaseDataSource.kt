package com.sicoapp.localrestaurants.data.local

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * @author ll4
 * @date 2/6/2021
 */
class DatabaseDataSource @Inject constructor(
    private val databaseDao: DatabaseDao
) {

    fun getRestaurant() = databaseDao.getAll()


    fun saveRestaurants(restaurant: Restaurant) {
        val observable: Observable<Restaurant>
        observable = Observable.just<Restaurant>(restaurant)
        observable.subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Restaurant?> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(restaurant: Restaurant) {
                    databaseDao.insertAll(restaurant)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            })
    }



    fun updateRestaurants(restaurant: Restaurant) {
        val observable: Observable<Restaurant>
        observable = Observable.just<Restaurant>(restaurant)
        observable.subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Restaurant?> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(restaurant: Restaurant) {
                    databaseDao.upadate(restaurant)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            })
    }
}