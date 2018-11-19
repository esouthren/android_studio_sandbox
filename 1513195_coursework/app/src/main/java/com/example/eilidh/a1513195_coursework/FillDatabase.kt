package com.example.eilidh.a1513195_coursework

import android.arch.persistence.room.Room
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import org.json.JSONObject
import java.util.*

class FillDatabase(mDb: WeatherDatabase, mDbWorkerThread: DbWorkerThread) {

    private var mDbWorkerThread = mDbWorkerThread
    private var mDb = mDb


    fun addDataToDatabase(data: ApiData.CoreData, context: Context, address: String) {
        // clear database
        //clearDatabase()
        //Log.i("debug", "adding data to database")
        //fetchWeatherDataFromDb(context)
        //Log.i("debug", data.toString())

        // coredata


        // data within each hour
        var hourCount = 0
        for (thisHour in data.hourly.data) {
            val weatherData = WeatherData(
                    Random().nextInt((100000000 + 1) - 1) + 1,
                    address,
                    data.latitude?.toDouble(),
                    data.longitude?.toDouble(),
                    hourCount,
                    thisHour.summary,
                    thisHour.icon?.toString(),
                    thisHour.time?.toString(),
                    thisHour.temperature?.toDouble(),
                    thisHour.precipProbability?.toDouble(),
                    thisHour.precipType,
                    thisHour.apparantTemperature?.toString(),
                    thisHour.humidity?.toDouble(),
                    thisHour.pressure?.toDouble(),
                    thisHour.windspeed?.toDouble(),
                    thisHour.windGust?.toDouble(),
                    thisHour.cloudCover?.toDouble(),
                    thisHour.uvIndex?.toDouble(),
                    thisHour.visibility?.toDouble()
            )
            insertWeatherDataInDb(weatherData)
            Log.i("debug", "hour: $hourCount : $weatherData")
            hourCount++
        }

        fetchWeatherDataFromDb(context)


    }

    private fun fetchWeatherDataFromDb(context: Context) {


        val task = Runnable {
            val weatherData =
                    mDb?.weatherDao()?.getAll()

            if (weatherData == null || weatherData?.size == 0) {
                Log.i("debug", "no data in database :(")
            } else {
                Log.i("debug", "there's some data!" + weatherData.size)

            }
        }
        mDbWorkerThread.postTask(task)

    }

    private fun insertWeatherDataInDb(weatherData: WeatherData) {
        Log.i("debug", "Inserting data...")
        val task = Runnable { mDb?.weatherDao()?.insert(weatherData) }
        mDbWorkerThread.postTask(task)
    }

    fun onDestroy() {
        WeatherDatabase.destroyInstance()
        mDbWorkerThread.quit()
        //super.onDestroy()
    }


    fun isConnectedToInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }


    fun clearDatabase() {
        val task = Runnable { mDb?.weatherDao()?.deleteAll() }
        mDbWorkerThread.postTask(task)
    }

}