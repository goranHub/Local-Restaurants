package com.sicoapp.localrestaurants.data.local.database

/**
 * @author ll4
 * @date 3/12/2021
 */
data class DatabaseEvent<T>(val eventType: DatabaseEventType, val value: T)
