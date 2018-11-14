package com.example.eilidh.a1513195_coursework

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "weatherData")
data class WeatherData(@PrimaryKey(autoGenerate = true) var id: Int,
                       @ColumnInfo(name = "temperature") var temperature: Double)
{
    constructor():this(0, 0.0)

}

