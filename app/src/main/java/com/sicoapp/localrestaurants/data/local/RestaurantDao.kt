package com.sicoapp.localrestaurants.data.local

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single


@Dao
interface RestaurantDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurant(vararg restaurant: Restaurant) : Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg restaurant: Restaurant)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateRestaurant(vararg restaurant: Restaurant)

    @Query("SELECT * FROM restaurant")
    fun getAll(): Single<List<Restaurant>>

    @Delete
    fun deleteRestaurant(restaurant: Restaurant): Completable


}