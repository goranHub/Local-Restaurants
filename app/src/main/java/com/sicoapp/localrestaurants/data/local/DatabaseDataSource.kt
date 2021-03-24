package com.sicoapp.localrestaurants.data.local

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

    private val mObserverSubject = PublishSubject.create<DatabaseEvent<Restaurant>>()


/*    fun saveRestaurants(restaurant: Restaurant): Completable? {
        val insertEvent = DatabaseEvent(DatabaseEventType.INSERTED, restaurant)
        return databaseDao.insertRestaurant(restaurant)
            .doOnComplete { mObserverSubject.onNext(insertEvent) }
    }*/

    fun saveRestaurants(restaurant: Restaurant) {
        val observable = Observable.just(restaurant)
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
        val observable = Observable.just(restaurant)
        observable.subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Restaurant?> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(restaurant: Restaurant) {
                    databaseDao.updateRestaurant(restaurant)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            })
    }


/*    fun updateRestaurants(restaurant: Restaurant): Completable {
        val updateEvent = DatabaseEvent(DatabaseEventType.UPDATED, restaurant)
        return databaseDao.updateRestaurant(restaurant)
            .doOnComplete { mObserverSubject.onNext(updateEvent) }
    }*/

/*    @SuppressLint("CheckResult")
    fun updateRestaurants(restaurant: Restaurant) {
        val observable = Observable.just(restaurant)
        observable.subscribeOn(Schedulers.io())
            .subscribe{
                databaseDao.update(it)
            }
    }*/

    fun deleteTask(restaurant: Restaurant): Completable {
        val deleteEvent = DatabaseEvent(DatabaseEventType.REMOVED, restaurant)
        return databaseDao.deleteRestaurant(restaurant)
            .doOnComplete { mObserverSubject.onNext(deleteEvent) }
    }
}