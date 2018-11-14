package com.example.eilidh.a1513195_coursework

import android.app.DownloadManager
import android.arch.persistence.room.Room
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val TAG = "debug"
    val APIKEY = "bd233fef7ea7953a843bbbb58fc087ba"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(
                applicationContext,
                Database::class.java, "weather-database"
        ).build()

    }

    fun callApi(view: View) {
        val web = "https://api.darksky.net/forecast"
        val lat = "42.3601"
        val long = "-71.0589"
        val excludes ="?exclude=minutely,alerts,flags" // things to remove from the api call
        val time = "2018-11-15T12:30:00Z"
        val slash = "/"
        val flag = "?"
        val delim = ","
        // api gets called here
        Log.i(TAG, "API being called!")
        val queue = Volley.newRequestQueue(this)
        val url = "$web$slash$APIKEY$slash$lat$delim$long$delim$time$flag$excludes"
        Log.i(TAG, url)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                val text = response
                Log.i(TAG, text.toString())
                displayData(response)
            },
        Response.ErrorListener { Log.i(TAG, "API Fail :( ") } )

    queue.add(jsonObjectRequest)

    }

    fun displayData(data: JSONObject) {
        val textView: TextView = findViewById(R.id.text_weather) as TextView
      // textView.text = data["temperature"].toString()
        var gson = Gson()
        Log.i(TAG, data.javaClass.name)
        var mine = gson.fromJson(data.toString(), ApiData.CoreData::class.java)
        Log.i(TAG, "TimeZone: " +  mine.timezone)
        Log.i(TAG, "Hourly summary: " + mine.hourly.summary)
        Log.i(TAG, "length of hours array: " + mine.hourly.data.size)

    }

    fun openPreferences(view: View) {
        // open user preferences
        val intent = Intent(this,UserPreferences::class.java).apply {

        }
        startActivity(intent)
    }
}
