package com.example.eilidh.a1513195_coursework

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(WeatherData::class), version = 1)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): WeatherDataDao
}