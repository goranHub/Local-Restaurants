package com.sicoapp.localrestaurants.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface DatabaseDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg restaurant: Restaurant)

    @Query("SELECT * FROM restaurant")
    fun getAll(): LiveData<List<Restaurant>>




}