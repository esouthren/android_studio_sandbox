package com.example.eilidh.a1513195_coursework

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.RawQuery
import android.arch.persistence.db.SupportSQLiteQuery




@Dao
interface WeatherDataDao {

    @Query("SELECT * from weatherData")
    fun getAll(): List<WeatherData>

    @Insert()
    fun insert(weatherData: WeatherData)

    @Query("DELETE from weatherData")
    fun deleteAll()

    // Get a preference (place) with one hour of data
    @Query("SELECT * FROM weatherData WHERE place_string = :place AND hour = :hour")
    fun getSingleHour(place: String, hour: Int): List<WeatherData>

    @Query("SELECT * FROM weatherData WHERE place_string = :place")
    fun getSinglePreferenceData(place: String): List<WeatherData>

    // Raw Query takes parameters of field, value and operator (< / > / =)
    @RawQuery
    fun getSearchQuery(query: SupportSQLiteQuery): List<WeatherData>
}