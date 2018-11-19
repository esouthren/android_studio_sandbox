package com.example.eilidh.a1513195_coursework

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.eilidh.a1513195_coursework.R.id.textView
import com.google.gson.Gson


import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val TAG = "debug"
    val APIKEY = "bd233fef7ea7953a843bbbb58fc087ba"
    var prefs: Prefs? = null
    private lateinit var mDbWorkerThread: DbWorkerThread
    private var mDb: WeatherDatabase? = null
    lateinit var fb: FillDatabase
    var currentPrefView = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = Prefs(this)
        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()
        mDb = WeatherDatabase.getInstance(this)
        fb = FillDatabase(mDb!!, mDbWorkerThread)
        displayDbData()

    }

    fun callApi(lat: String, long: String, address: String) {
        val web = "https://api.darksky.net/forecast"
        val excludes = "?exclude=minutely,alerts,flags" // things to remove from the api call
        val time = "2018-11-15T12:30:00Z"
        val slash = "/"
        val flag = "?"
        val delim = ","
        // api gets called here
        //Log.i(TAG, "API being called!")
        val queue = Volley.newRequestQueue(this)
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
        // textView.text = data["temperature"].toString()
        // textView.text = data["temperature"].toString()
        var gson = Gson()
        //Log.i("debug", data.toString())
        // fill an ApiData object with Json data
        var parsedApiData = gson.fromJson(data.toString(), ApiData.CoreData::class.java)

        //Log.i(TAG, "Hourly summary: " + parsedApiData.hourly.summary)
        //Log.i(TAG, "length of hours array: " + parsedApiData.hourly.data.size)

        // todo: go through lab!
        fb.addDataToDatabase(parsedApiData, applicationContext, address)

    }

    fun openPreferences(view: View) {
        // open user preferences
        val intent = Intent(this, SetUserPreferences::class.java).apply {

        }
        startActivity(intent)
    }

    fun updateWeatherData(view: View) {
        Log.i(TAG, "emptying database")
        // toDo: empty DB of all values to be replaced with these
        // todo check internet connection before clearing database
        // don't call aPIs unless there's an internet connection

        fb.clearDatabase()
        // call api for each user preference
        for (i in 0..9) {
            var p = prefs?.getPrefLatLong(i)
            if (p!!.length > 1) {
                // if a lat/long exists
                val pSplit = p.split("\n")
                val address = prefs!!.getPrefAddress(i)
                Log.i("debug", "calling api for $address")
                callApi(pSplit[0], pSplit[1], address)

            }
        }
        // update data
        displayDbData()

    }

    fun displayDbData() {
        Log.i(TAG, "displaying data in DB...")
        // check db for data, if it contains data, display it?

        val currentPlace = prefs!!.getPrefAddress(currentPrefView)

        // get 0th (current) hour
        val rightNow = getPreferenceWeather(currentPlace)
        //Log.i("debug", rightnow!!.size.toString())
        //Log.i("debug", rightnow!!.toString())

    }

    fun displayEmptyDatabaseScreen() {
        Log.i(TAG, "empty database screen")
        // hide windows and add a text box saying 'no data stored'
        findViewById<TextView>(R.id.preference_title).setText("")
        findViewById<TextView>(R.id.preference_summary).setText("No Data to Display")
        //findViewById<TextView>(R.id.preference_title).setText("")
        //findViewById<TextView>(R.id.preference_title).setText("")


    }

    fun getPreferenceWeather(address: String) {
        Log.i("debug", "\n\ngetPreferenceWeather")
        val task = Runnable {
            val currentWeather = mDb?.weatherDao()?.getSinglePreferenceData(address)
            if (currentWeather!!.isEmpty()) {
                Log.i("debug", "empty data")
                runOnUiThread(
                        object : Runnable {
                            override fun run() {
                                displayEmptyDatabaseScreen()
                            }
                        }
                )

            } else {
                runOnUiThread(
                        object : Runnable {
                            override fun run() {
                                Log.i("debug", "got current weather: " + currentWeather!!.get(0).summary)
                                setPreferenceTitle(currentWeather.get(0).placeString!!)
                                setPreferenceSummary(currentWeather.get(0).summary!!)
                            }
                        }
                )

            }

        }
        mDbWorkerThread.postTask(task)
    }

    fun setPreferenceTitle(place: String) {
        findViewById<TextView>(R.id.preference_title).setText(place)
    }

    fun setPreferenceSummary(summary: String) {
        findViewById<TextView>(R.id.preference_summary).setText(summary)
    }
}
