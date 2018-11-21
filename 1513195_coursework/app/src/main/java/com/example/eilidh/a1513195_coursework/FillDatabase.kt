package com.example.eilidh.a1513195_coursework

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class FillDatabase(mDb: WeatherDatabase, mDbWorkerThread: DbWorkerThread, prefs: UserPreferences, activity: Activity) {

    private var mDbWorkerThread = mDbWorkerThread
    private var mDb = mDb
    private var prefs = prefs
    private var activity = activity


    fun addDataToDatabase(data: ApiData.CoreData, context: Context, address: String) {
        Log.i("debug", "4) adddataToDatabase")
        var hourCount = 0

        for (thisHour in data.hourly.data) {
            val weatherData = WeatherData(
                    Random().nextInt((100000000 + 1) - 1) + 1,
                    address,
                    data.latitude?.toDouble(),
                    data.longitude?.toDouble(),
                    hourCount,
                    thisHour.summary,
                    thisHour.icon,
                    thisHour.time?.toString(),
                    convertToCelcius(thisHour.temperature?.toDouble()),
                    thisHour.precipProbability?.toDouble(),
                    thisHour.precipType,
                    convertToCelcius(thisHour.apparantTemperature?.toDouble()),
                    thisHour.humidity?.toDouble(),
                    thisHour.pressure?.toDouble(),
                    thisHour.windspeed?.toDouble(),
                    thisHour.windGust?.toDouble(),
                    thisHour.cloudCover?.toDouble(),
                    thisHour.uvIndex?.toDouble(),
                    thisHour.visibility?.toDouble()
            )
            insertWeatherDataInDb(weatherData)
            hourCount++
        }
    }

    private fun insertWeatherDataInDb(weatherData: WeatherData) {
        Log.i("debug", "5) insertWeatherDataInDb")
        val task = Runnable { mDb?.weatherDao()?.insert(weatherData)
            displayDbData()
        }
        mDbWorkerThread.postTask(task)
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

    fun convertToCelcius(f: Double?): Double {
        if(f==null) {
            return 0.0
        }
        else {
            return ((f - 32.0) * (5.0 / 9.0))
        }
    }

    fun displayDbData() {
        val userPreferences = prefs?.getNumberOfPreferences()
        if (userPreferences == 0) {
            // display popup error message
            Log.i("debug", "no preferences!")
        } else {
            val currentPlace = prefs!!.getPrefAddress(prefs!!.getCurrentPrefView())
            getPreferenceWeather(currentPlace)
        }

    }


    fun getPreferenceWeather(address: String) {
        val task = Runnable {
            val currentWeather = mDb?.weatherDao()?.getSinglePreferenceData(address)
            if (currentWeather!!.isEmpty()) {
                Log.i("debug", "getPreferenceWeather: no database data ")
                activity.runOnUiThread(
                        object : Runnable {
                            override fun run() {
                                displayEmptyDatabaseScreen()
                            }
                        }
                )

            } else {
                activity.runOnUiThread(
                        object : Runnable {
                            override fun run() {
                                Log.i("debug", "getPreferenceWeather: got current weather: " + currentWeather!!.get(0).summary)
                                setPreferenceTitle(currentWeather.get(0).placeString!!)
                                setPreferenceSummary(currentWeather.get(0).summary!!)
                                Log.i("debug", "TemperaturE: " + currentWeather.get(0).temperature)
                                setPreferenceTemp(currentWeather.get(0).temperature!!)
                                setPreferenceRainChance(currentWeather.get(0).precipProbability!!)
                                setIcon(currentWeather.get(0).icon!!)
                            }
                        }
                )

            }

        }
        mDbWorkerThread.postTask(task)
    }

    fun setPreferenceTitle(place: String) {
        activity.findViewById<TextView>(R.id.preference_title).setText(place)
    }

    fun setPreferenceSummary(summary: String) {
        activity.findViewById<TextView>(R.id.preference_summary).setText(summary)
    }

    fun setPreferenceTemp(temp: Double) {
        val rounded = Math.round(temp)
        val degree = Typography.degree

        activity.findViewById<TextView>(R.id.preference_temp).setText("$rounded$degree")
    }

    fun setPreferenceRainChance(chance: Double) {
        val edit = Math.round(chance * 10)
        activity.findViewById<TextView>(R.id.preference_chance_rain).setText("$edit%")

    }

    fun setIcon(weatherType: String) {
        var icon = R.mipmap.cloudy
        when(weatherType) {
            "clear-day" -> icon = R.mipmap.sun
            "clear-night" -> icon = R.mipmap.sun
            "rain" -> icon = R.mipmap.rain
            "snow" -> icon = R.mipmap.snow
            "sleet" -> icon = R.mipmap.sleet
            "wind" -> icon = R.mipmap.partly_cloudy
            "fog" -> icon = R.mipmap.foggy
            "cloudy" -> icon = R.mipmap.cloudy
            "partly-cloudy-day" -> icon = R.mipmap.partly_cloudy
            "partly-cloudy-night" -> icon = R.mipmap.partly_cloudy
            else -> icon = R.mipmap.partly_cloudy
        }
        activity.findViewById<ImageView>(R.id.preference_icon).setImageResource(icon)
    }

    fun displayEmptyDatabaseScreen() {
        Log.i("debug", "empty database screen")
        // hide windows and add a text box saying 'no data stored'
        activity.findViewById<TextView>(R.id.preference_title).setText("")
        activity.findViewById<TextView>(R.id.preference_summary).setText("No Data to Display")
        activity.findViewById<TextView>(R.id.preference_temp).setText("")
        activity.findViewById<TextView>(R.id.preference_chance_rain).setText("")

    }




}