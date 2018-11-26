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

    @Query("SELECT * FROM weatherData WHERE :attribute = :value")
    fun getSearchDataEqualTo(attribute: String, value: Double): List<WeatherData>

    @Query("SELECT * FROM weatherData WHERE :attribute < :value")
    fun getSearchDataLessThan(attribute: String, value: Double): List<WeatherData>

    @Query("SELECT place_string, time, uid, hour, :attribute  FROM weatherData WHERE :attribute > :value")
    fun getSearchDataGreaterThan(attribute: String, value: Double): List<WeatherData>

    @RawQuery
    fun getSearchQuery(query: SupportSQLiteQuery): List<WeatherData>

}