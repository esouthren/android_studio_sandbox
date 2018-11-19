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

class FillDatabase(mDb: WeatherDatabase, mDbWorkerThread: DbWorkerThread) {

    private var mDbWorkerThread = mDbWorkerThread
    private var mDb = mDb


    fun addDataToDatabase(data: ApiData.CoreData, context: Context) {
        fetchWeatherDataFromDb(context)
        /*
        Log.i("debug", "hello!")
        mDbWorkerThread = DbWorkerThread("dbWorkerThread")

        //var gson = Gson()
        //var jsonData = gson.fromJson(data.toString(), ApiData.CoreData::class.java)
        //var weatherData = WeatherData()

        //val task = Runnable { db?.userDao()?.insert(weatherData) }
        //thread.postTask(task)

        // todo: add internet connectivity check
        val weatherData = WeatherData(123,"Test!",0.0,0.0,0,"","","",0.0)
        insertWeatherDataInDb(weatherData)
*/

}

private fun fetchWeatherDataFromDb(context: Context) {


    val task = Runnable {
        val weatherData =
                mDb?.weatherDao()?.getAll()

            if (weatherData == null || weatherData?.size == 0) {
                Log.i("debug", "no data in database :(")
            } else {
                Log.i("debug", "there's some data!" + weatherData[0])

            }
    }
    mDbWorkerThread.postTask(task)

}

private fun insertWeatherDataInDb(weatherData: WeatherData) {
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
}
