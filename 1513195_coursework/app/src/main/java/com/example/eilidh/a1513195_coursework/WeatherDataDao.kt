package com.example.eilidh.a1513195_coursework

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface WeatherDataDao {

    @Query("SELECT * from weatherData")
    fun getAll(): List<WeatherData>

    @Insert()
    fun insert(weatherData: WeatherData)

    @Query("DELETE from weatherData")
    fun deleteAll()
}