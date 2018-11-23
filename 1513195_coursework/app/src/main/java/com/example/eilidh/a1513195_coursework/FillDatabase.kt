package com.example.eilidh.a1513195_coursework

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.Gravity
import android.widget.*
import java.util.*
import android.widget.LinearLayout



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
            insertWeatherDataInDb(weatherData, context)
            hourCount++
        }
    }

    private fun insertWeatherDataInDb(weatherData: WeatherData, context: Context) {
        Log.i("debug", "5) insertWeatherDataInDb")
        val task = Runnable { mDb?.weatherDao()?.insert(weatherData)
            displayDbData(context)
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

    fun displayDbData(context: Context) {
        val userPreferences = prefs?.getNumberOfPreferences()
        if (userPreferences == 0) {
            // display popup error message
            Log.i("debug", "no preferences!")
        } else {
            val currentPlace = prefs!!.getPrefAddress(prefs!!.getCurrentPrefView())
            getPreferenceWeather(currentPlace, context)
        }

    }


    fun getPreferenceWeather(address: String, context: Context) {
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
                                setHourlyScrollViewContents(currentWeather, context)
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

        activity.findViewById<ImageView>(R.id.preference_icon).setImageResource(getIcon(weatherType))
    }

    fun getIcon(weatherType: String) : Int {
        when(weatherType) {
            "clear-day" -> return R.mipmap.sun
            "clear-night" -> return R.mipmap.sun
            "rain" -> return R.mipmap.rain
            "snow" -> return R.mipmap.snow
            "sleet" -> return R.mipmap.sleet
            "wind" -> return R.mipmap.partly_cloudy
            "fog" -> return R.mipmap.foggy
            "cloudy" -> return R.mipmap.cloudy
            "partly-cloudy-day" -> return R.mipmap.partly_cloudy
            "partly-cloudy-night" -> return R.mipmap.partly_cloudy
            else -> return R.mipmap.partly_cloudy
        }
    }

    fun displayEmptyDatabaseScreen() {
        Log.i("debug", "empty database screen")
        // hide windows and add a text box saying 'no data stored'
        activity.findViewById<TextView>(R.id.preference_title).setText("")
        activity.findViewById<TextView>(R.id.preference_summary).setText("No Data to Display")
        activity.findViewById<TextView>(R.id.preference_temp).setText("")
        activity.findViewById<TextView>(R.id.preference_chance_rain).setText("")

    }

    fun setHourlyScrollViewContents(data: List<WeatherData>, context: Context) {
        Log.i("debug", "setting hourly scroll view")
        val scrollView = activity.findViewById<LinearLayout>(R.id.hourly_linearView)
        for(i in 1..23) {
            val hourSlot: LinearLayout = LinearLayout(context)
            hourSlot.setOrientation(LinearLayout.VERTICAL)
            val hourSlotParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
            //hourSlot.layoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            hourSlot.layoutParams = hourSlotParams
            hourSlot.gravity = Gravity.CENTER

            val timeLayout = RelativeLayout(context)
            val timeLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            timeLayout.setLayoutParams(timeLayoutParams)
            val hourTime = TextView(context)
            hourTime.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            hourTime.setText("Time")
            timeLayout.addView(hourTime)

            val iconLayout = RelativeLayout(context)
            val iconLayoutParams = LinearLayout.LayoutParams(100, 100, 1f)
            iconLayout.setLayoutParams(iconLayoutParams)
            val icon = ImageView(context)

            icon.setImageResource(getIcon(data.get(i).summary!!))
            iconLayout.addView(icon)


            val tempLayout = RelativeLayout(context)
            val tempLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            tempLayout.setLayoutParams(tempLayoutParams)
            //tempLayout.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            val hourTemp = TextView(context)
            //hourTemp.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            val rounded = Math.round(data.get(i).temperature!!)
            val degree = Typography.degree

            hourTemp.setText("$rounded$degree")
            //hourTemp.setEms(3)
            tempLayout.addView(hourTemp)
            //hourTemp.setText("brashh!!")
            hourSlot.addView(timeLayout)
            hourSlot.addView(iconLayout)
            hourSlot.addView(tempLayout)

//setting image position
           // icon.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                //    LinearLayout.LayoutParams.WRAP_CONTENT))
            scrollView.addView(hourSlot)
        }
    }




}