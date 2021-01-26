package com.sicoapp.localrestaurants.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


/**
 * @author ll4
 * @date 12/15/2020
 */
@Database(entities = [Restaurant::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun movieDao() : DatabaseDao
}