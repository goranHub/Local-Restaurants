package com.sicoapp.localrestaurants.data.local

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single



@Dao
interface RestaurantDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurant(vararg restaurant: RestaurantEntity) : Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg restaurant: RestaurantEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateRestaurant(vararg restaurant: RestaurantEntity)

    @Query("SELECT * FROM RestaurantEntity")
    fun getAllSingle(): Single<List<RestaurantEntity>>

    @Query("SELECT * FROM RestaurantEntity")
    fun getAll(): Flowable<List<RestaurantEntity>>

    @Delete
    fun deleteRestaurant(restaurant: RestaurantEntity): Completable


}