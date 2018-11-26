package com.example.eilidh.a1513195_coursework

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.*

import com.google.gson.Gson


import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val TAG = "debug"
    val APIKEY = "bd233fef7ea7953a843bbbb58fc087ba"
    var prefs: UserPreferences? = null
    private lateinit var mDbWorkerThread: DbWorkerThread
    var onlineChecker: OnlineChecker = OnlineChecker()
    private var mDb: WeatherDatabase? = null
    lateinit var fb: FillDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = UserPreferences(this)
        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()
        //onlineChecker = OnlineChecker()


        mDb = WeatherDatabase.getInstance(this)
        fb = FillDatabase(mDb!!, mDbWorkerThread, prefs!!, this@MainActivity)

        fb.displayDbData(this@MainActivity)
        prefs!!.setCurrentPrefView(0)
        Log.i("debug", "current pref view: " + prefs!!.getCurrentPrefView())
    }

    fun changePreferenceViewLeft(view: View) {
        changePreferenceView("left")
    }

    fun changePreferenceViewRight(view: View) {
        changePreferenceView("right")
    }


    fun changePreferenceView(direction: String) {
        //todo: this doesn't seem to work? maybe need to thread?
        // move the view to another user preference
        val numberOfFilledPreferences = prefs!!.getNumberOfPreferences()
        Log.i("debug", "number of preferences: " + prefs!!.getNumberOfPreferences().toString())
        Log.i("debug", "changing preferenceview to the: " + direction)
        val currentPlace = prefs!!.getPrefAddress(prefs!!.getCurrentPrefView())
        val currentPrefView = prefs!!.getCurrentPrefView()

        // user clicks 'left' button
        if(direction.equals("left")) {
            if(currentPrefView>0) {
                    prefs!!.setCurrentPrefView((currentPrefView - 1))
                    Log.i("debug", "new preference: " + prefs!!.getPrefAddress(prefs!!.getCurrentPrefView()))
            }
            else {
                // circle around to the last user preference
                prefs!!.setCurrentPrefView(numberOfFilledPreferences-1)
            }
        } else {
            // user clicks 'right' button
            if (currentPrefView < numberOfFilledPreferences-1) {
                val newPref = currentPrefView + 1
                prefs!!.setCurrentPrefView(newPref)
                Log.i("debug", "new preference: " + prefs!!.getPrefAddress(prefs!!.getCurrentPrefView()))
                fb.getPreferenceWeather(prefs!!.getPrefAddress(currentPrefView), this@MainActivity)

            } else {
                // circle back to first preference
                prefs!!.setCurrentPrefView(0)
            }
        }

        fb.getPreferenceWeather(prefs!!.getPrefAddress(currentPrefView), this@MainActivity)

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
        Log.i("debug", "3) parseJsonDataToApiData")
        // textView.text = data["temperature"].toString()
        // textView.text = data["temperature"].toString()
        var gson = Gson()
        //Log.i("debug", data.toString())
        // fill an ApiData object with Json data
        var parsedApiData = gson.fromJson(data.toString(), ApiData.CoreData::class.java)

        fb.addDataToDatabase(parsedApiData, applicationContext, address)

    }

    fun openPreferences(view: View) {
        // open user preferences activity
        val intent = Intent(this, SetUserPreferences::class.java).apply {
        }
        startActivity(intent)
    }

    fun updateWeatherData(view: View) {

        Log.i("debug", "Refresh Data button pressed")
        // check to see if there are user preferences
        if(!prefs!!.hasPreferences()) {
            // display error about having no preferences
            prefs!!.displayNoPreferencesError(view)
        } else {
            if (onlineChecker.isOnline(this@MainActivity)) {
                // todo check internet connection before clearing database
                // don't call aPIs unless there's an internet connection
                fb.clearDatabase()

                val task = Runnable {
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
}
