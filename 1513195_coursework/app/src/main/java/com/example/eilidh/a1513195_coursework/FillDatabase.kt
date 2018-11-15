package com.example.eilidh.a1513195_coursework

import android.arch.persistence.room.Room
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject

class FillDatabase() {

    val db = Room.databaseBuilder(,
            Database::class.java, "weather-database"
    ).build()

    fun helloWorld(tag: String) {
        Log.i(tag, "Hello!")
    }

    fun addDataToDatabase(data: JSONObject, db: Database, thread: DbWorkerThread) {

        //var gson = Gson()
        //var jsonData = gson.fromJson(data.toString(), ApiData.CoreData::class.java)
        //var weatherData = WeatherData()

        //val task = Runnable { db?.userDao()?.insert(weatherData) }
        //thread.postTask(task)
    }

}
