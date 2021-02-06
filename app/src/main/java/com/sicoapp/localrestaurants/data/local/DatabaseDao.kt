package com.sicoapp.localrestaurants.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface DatabaseDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRestaurant(forecast: Restaurant)

    @Query("SELECT * FROM restaurant")
    fun getRestaurant(): LiveData<List<Restaurant>>


}