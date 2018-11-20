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

import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


import org.json.JSONObject
import java.lang.Math.round

class MainActivity : AppCompatActivity() {

    val TAG = "debug"
    val APIKEY = "bd233fef7ea7953a843bbbb58fc087ba"
    var prefs: Prefs? = null
    private lateinit var mDbWorkerThread: DbWorkerThread

    private var mDb: WeatherDatabase? = null
    lateinit var fb: FillDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = Prefs(this)
        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()


        mDb = WeatherDatabase.getInstance(this)
        fb = FillDatabase(mDb!!, mDbWorkerThread, prefs!!,this@MainActivity)

        displayDbData()
        prefs!!.setCurrentPrefView(0)
        Log.i("debug", "current pref view: " + prefs!!.getCurrentPrefView())

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
        // Look at RunBlocking docs
        //runBlocking {
           // launch(coroutineContext) {
                val task = Runnable {
                    // call api for each user preference
                    for (i in 0..9) {
                        var p = prefs?.getPrefLatLong(i)
                        Log.i("debug", "1) calling api for: " + prefs?.getPrefAddress(i))
                        if (p!!.length > 1) {
                            // if a lat/long exists
                            val pSplit = p.split("\n")
                            val address = prefs!!.getPrefAddress(i)
                            Log.i("debug", "calling api for $address")
                            callApi(pSplit[0], pSplit[1], address)

                        }
                    }

                }
                mDbWorkerThread.postTask(task)
         //   }.join()
          //  run {

                displayDbData()
          //  }
        }




        //Thread.sleep(1000)
        //mDbWorkerThread.join()
        //displayDbData()

    //}

    fun displayDbData() {

        Log.i(TAG, "6) displayData: displaying data in DB...")
        // check db for data, if it contains data, display it?
        val c = prefs!!.getCurrentPrefView()
        val currentPlace = prefs!!.getPrefAddress(c)

        // get 0th (current) hour
        getPreferenceWeather(currentPlace)
        //Log.i("debug", rightnow!!.size.toString())
        //Log.i("debug", rightnow!!.toString())

    }

    fun displayEmptyDatabaseScreen() {
        Log.i(TAG, "empty database screen")
        // hide windows and add a text box saying 'no data stored'
        findViewById<TextView>(R.id.preference_title).setText("")
        findViewById<TextView>(R.id.preference_summary).setText("No Data to Display")
        findViewById<TextView>(R.id.preference_temp).setText("")
        findViewById<TextView>(R.id.preference_chance_rain).setText("")


    }

    fun getPreferenceWeather(address: String) {
        val task = Runnable {
            val currentWeather = mDb?.weatherDao()?.getSinglePreferenceData(address)
            if (currentWeather!!.isEmpty()) {
                Log.i("debug", "getPreferenceWeather: no database data ")
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
                                Log.i("debug", "getPreferenceWeather: got current weather: " + currentWeather!!.get(0).summary)
                                setPreferenceTitle(currentWeather.get(0).placeString!!)
                                setPreferenceSummary(currentWeather.get(0).summary!!)
                                Log.i("debug", "TemperaturE: " + currentWeather.get(0).temperature)
                                setPreferenceTemp(currentWeather.get(0).temperature!!)
                                setPreferenceRainChance(currentWeather.get(0).precipProbability!!)
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

    fun setPreferenceTemp(temp: Double) {
        val rounded = round(temp)
        val degree = Typography.degree

        findViewById<TextView>(R.id.preference_temp).setText("$rounded$degree")
    }

    fun setPreferenceRainChance(chance: Double) {
        val edit = round(chance*10)
        findViewById<TextView>(R.id.preference_chance_rain).setText("$edit%")

    }
}
