package com.sicoapp.localrestaurants.data.local.database

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


/**
 * @author ll4
 * @date 2/6/2021
 */
class DatabaseDataSource @Inject constructor(
    private val databaseDao: RestaurantDao
) {

    fun getRestaurant() = databaseDao.getAll()

    fun getRestaurantSingle() = databaseDao.getAllSingle()

    private val mObserverSubject = PublishSubject.create<DatabaseEvent<RestaurantEntity>>()

    fun saveRestaurants(restaurant: RestaurantEntity) {
        val observable = Observable.just(restaurant)
        observable.subscribeOn(Schedulers.io())
            .subscribe(object : Observer<RestaurantEntity?> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(restaurant: RestaurantEntity) {
                    databaseDao.insertAll(restaurant)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            })
    }

    fun addRestaurant(restaurantEntity: RestaurantEntity){
        val observable = Observable.just(restaurantEntity)
        observable.subscribeOn(Schedulers.io())
            .subscribe(object : Observer<RestaurantEntity?> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(restaurant: RestaurantEntity) {
                    databaseDao.addRestaurant(restaurant)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            })
    }

    fun updateRestaurants(restaurantEntity: RestaurantEntity) {
        val observable = Observable.just(restaurantEntity)
        observable.subscribeOn(Schedulers.io())
            .subscribe(object : Observer<RestaurantEntity?> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(restaurant: RestaurantEntity) {
                    databaseDao.updateRestaurant(restaurant)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            })
    }


    fun deleteTask(restaurant: RestaurantEntity): Completable {
        val deleteEvent = DatabaseEvent(DatabaseEventType.REMOVED, restaurant)
        return databaseDao.deleteRestaurant(restaurant)
            .doOnComplete { mObserverSubject.onNext(deleteEvent) }
    }
}