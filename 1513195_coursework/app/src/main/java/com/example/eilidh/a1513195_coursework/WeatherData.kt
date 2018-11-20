package com.example.eilidh.a1513195_coursework

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "weatherData")
data class WeatherData(@PrimaryKey(autoGenerate = true) var uid: Int,
                       @ColumnInfo(name = "place_string") var placeString: String?,
                       @ColumnInfo(name = "latitude") var latitude: Double?,
                       @ColumnInfo(name = "longitude") var longitude: Double?,
                       @ColumnInfo(name = "hour") var hour: Int, // 0 = current, possible values 0 - 24
                       @ColumnInfo(name = "summary") var summary: String?,
                       @ColumnInfo(name = "icon") var icon: String?,
                       @ColumnInfo(name = "time") var time: String?,
                       @ColumnInfo(name = "temperature") var temperature: Double?,
                       @ColumnInfo(name = "precipProbaility") var precipProbability: Double?,
                       @ColumnInfo(name = "precipType") var precipType: String?,
                       @ColumnInfo(name = "apparantTemperature") var apparantTemperature: Double?,
                       @ColumnInfo(name = "humidity") var humidity: Double?,
                       @ColumnInfo(name = "pressure") var pressure: Double?,
                       @ColumnInfo(name = "windspeed") var windspeed: Double?,
                       @ColumnInfo(name = "windGust") var windGust: Double?,
                       @ColumnInfo(name = "cloudCover") var cloudCover: Double?,
                       @ColumnInfo(name = "uvIndex") var uvIndex: Double?,
                       @ColumnInfo(name = "visibility") var visibility: Double?)


                {
                constructor():this(0,"",0.0,0.0,0,
                        "","","",0.0, 0.0,
                        "", 0.0, 0.0, 0.0,
                        0.0,0.0,0.0,0.0,0.0)
                }
// each database entry is city + time
