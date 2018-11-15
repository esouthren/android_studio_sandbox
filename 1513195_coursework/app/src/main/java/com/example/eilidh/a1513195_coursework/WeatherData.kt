package com.example.eilidh.a1513195_coursework

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "weatherData")
data class WeatherData(@PrimaryKey() var uid: Int,
                       @ColumnInfo(name = "place_string") var placeString: String,
                       @ColumnInfo(name = "latitude") var latitude: Double,
                       @ColumnInfo(name = "longitude") var longitude: Double,
                       @ColumnInfo(name = "hour") var hour: Int, // 0 = current, possible values 0 - 24
                       @ColumnInfo(name = "summary") var summary: String,
                       @ColumnInfo(name = "icon") var icon: String,
                       @ColumnInfo(name = "time") var time: String,
                       @ColumnInfo(name = "temperature") var temperature: Double)
                        {
                        constructor():this(0,"",0.0,0.0,0,"","","",0.0)
                        }
// each database entry is city + time

/*
e.g. AberdeenHour0      lat=x long=x
            var latitude: Float,
            var longitude: Float,
            var timezone: String,
            var hourly: Hourly
    data class Hourly(
            var summary: String,
            var icon: String,
            var data: Array<HourData>
    data class HourData(
            var time: Long,
            var precipProbability: Float,
            var precipType: String,
            var temperature: Float,
            var summary: String, // e.g. "Partly Cloudy"
            var icon: String,
            var apparantTemperature: Float, // 'feels like...'
            var humidity: Float,
            var pressure: Float,
            var windspeed: Float,
            var windGust: Float,
            var cloudCover: Float,
            var uvIndex: Long,
            var visibility: Float
 */