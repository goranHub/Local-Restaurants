package com.sicoapp.localrestaurants.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Single


@Dao
interface DatabaseDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg restaurant: Restaurant)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun upadate(vararg restaurant: Restaurant)

    @Query("SELECT * FROM restaurant")
    fun getAll(): Single<List<Restaurant>>




}