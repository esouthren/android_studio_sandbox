package com.example.eilidh.a1513195_coursework

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import java.util.*
import android.widget.LinearLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import java.text.SimpleDateFormat


class FillDatabase(mDb: WeatherDatabase, mDbWorkerThread: DbWorkerThread, prefs: UserPreferences, activity: Activity, context: Context) {

    val TAG = "debug"
    val APIKEY = "bd233fef7ea7953a843bbbb58fc087ba"
    private var mDbWorkerThread = mDbWorkerThread
    private var mDb = mDb
    private var prefs = prefs
    private var activity = activity
    var onlineChecker: OnlineChecker = OnlineChecker()
    var context = context

    fun updateWeatherData(view: View) {

        Log.i("debug", "Refresh Data button pressed")
        // check to see if there are user preferences
        if(!prefs!!.hasPreferences()) {
            // display error about having no preferences
            prefs!!.displayNoPreferencesError(view)
        } else {
            if (onlineChecker.isOnline(context)) {
                clearDatabase()

                val task = kotlinx.coroutines.Runnable {
                    // call api for each user preference
                    for (i in 0..9) {
                        var p = prefs?.getPrefLatLong(i)
                        if (p!!.length > 1) {
                            // if a lat/long exists
                            val pSplit = p.split("\n")
                            val address = prefs!!.getPrefAddress(i)
                            callApi(pSplit[0], pSplit[1], address)
                        }
                    }
                }
                mDbWorkerThread.postTask(task)
            } else {
                onlineChecker.displayOfflineError(view)
            }
        }
    }

    fun callApi(lat: String, long: String, address: String) {
        Log.i("debug", "2) CallApi()")
        val web = "https://api.darksky.net/forecast"
        val excludes = "exclude=minutely,alerts,flags" // things to remove from the api call
        val time = "2018-11-15T12:30:00Z"
        val slash = "/"
        val flag = "?"
        val delim = ","
        // api gets called here
        //Log.i(TAG, "API being called!")
        val queue = Volley.newRequestQueue(context)
        val url = "$web$slash$APIKEY$slash$lat$delim$long$delim$time$flag$excludes"
        Log.i(TAG, url)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    val text = response
                    Log.i(TAG, text.toString())
                    parseJsonDataToApiData(response, address)
                },
                Response.ErrorListener { Log.i(TAG, "API Fail :( ") })

        queue.add(jsonObjectRequest)

    }


    fun parseJsonDataToApiData(data: JSONObject, address: String) {
        Log.i("debug", "3) parseJsonDataToApiData")
        // textView.text = data["temperature"].toString()
        // textView.text = data["temperature"].toString()
        var gson = Gson()
        //Log.i("debug", data.toString())
        // fill an ApiData object with Json data
        var parsedApiData = gson.fromJson(data.toString(), ApiData.CoreData::class.java)

        addDataToDatabase(parsedApiData, context, address)

    }

    fun addDataToDatabase(data: ApiData.CoreData, context: Context, address: String) {
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
        val task = Runnable {
            mDb?.weatherDao()?.insert(weatherData)
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
        if (f == null) {
            return 0.0
        } else {
            return ((f - 32.0) * (5.0 / 9.0))
        }
    }

    fun displayDbData(context: Context) {
        val userPreferences = prefs?.getNumberOfPreferences()
        if (userPreferences == 0) {
            // display popup error message
        } else {
            val currentPlace = prefs!!.getPrefAddress(prefs!!.getCurrentPrefView())
            getPreferenceWeather(currentPlace, context)
        }
    }

    fun getPreferenceWeather(address: String, context: Context) {
        Log.i("debug", "displaying weather for: $address")
        val task = Runnable {
            var currentWeather = mDb?.weatherDao()?.getSinglePreferenceData(address)
            if (currentWeather!!.isEmpty()) {
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
                                currentWeather = currentWeather.sortedBy { it.time }
                                setHourlyScrollViewContents(currentWeather)
                                setPreferenceTitle(currentWeather.get(0).placeString!!)
                                setPreferenceSummary(currentWeather.get(0).summary!!)
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
        activity.findViewById<ImageView>(R.id.preference_icon).setImageResource(getIcon(weatherType))
    }

    fun getIcon(weatherType: String): Int {
        // translate the data 'summary' to an appropriate weather icon
        when (weatherType) {
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


    fun setHourlyScrollViewContents(data: List<WeatherData>) {
        Log.i("debug", "setting new hourly data for: " + data.get(0).placeString)

        val scrollView = activity.findViewById<LinearLayout>(R.id.hourly_linearView)
        // clear previous data

        scrollView.removeAllViews()
        for (i in 1..data.size-1) {
            val hourSlot: LinearLayout = LinearLayout(activity)
            hourSlot.setOrientation(LinearLayout.VERTICAL)
            val hourSlotParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
            hourSlot.layoutParams = hourSlotParams
            hourSlot.gravity = Gravity.CENTER

            val timeLayout = RelativeLayout(activity)
            val timeLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            timeLayout.setLayoutParams(timeLayoutParams)
            val hourTime = TextView(activity)
            hourTime.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            val time = getHourString(data.get(i).time!!)
            hourTime.setText(time)
            timeLayout.addView(hourTime)

            val iconLayout = RelativeLayout(activity)
            val iconLayoutParams = LinearLayout.LayoutParams(100, 100, 1f)
            iconLayout.setLayoutParams(iconLayoutParams)
            val icon = ImageView(activity)

            icon.setImageResource(getIcon(data.get(i).icon!!))
            iconLayout.addView(icon)


            val tempLayout = RelativeLayout(activity)
            val tempLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            tempLayout.setLayoutParams(tempLayoutParams)
            val hourTemp = TextView(activity)
            val rounded = Math.round(data.get(i).temperature!!)
            val degree = Typography.degree

            hourTemp.setText("$rounded$degree")
            tempLayout.addView(hourTemp)
            hourSlot.addView(timeLayout)
            hourSlot.addView(iconLayout)
            hourSlot.addView(tempLayout)

            scrollView.addView(hourSlot)
        }
    }

    fun getHourString(time: String): String {
        try {
            val sdf = SimpleDateFormat("HHmm")
            val netDate = Date(time.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    fun searchDataLessThan(attribute: String, value: Float) {
        mDb!!.weatherDao().getSearchDataLessThan(attribute, value)
    }



}