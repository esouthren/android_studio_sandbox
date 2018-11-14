package com.example.eilidh.a1513195_coursework

import android.app.DownloadManager
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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "debug"
    val APIKEY = "bd233fef7ea7953a843bbbb58fc087ba"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun callApi(view: View) {
        // api gets called here
        Log.i(TAG, "API being called!")
        val queue = Volley.newRequestQueue(this)
        val url ="https://api.darksky.net/forecast/" + APIKEY + "/42.3601,-71.0589"
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                val text = response
                Log.i(TAG, text)
                displayData(response)
            },
        Response.ErrorListener { Log.i(TAG, "API Fail :( ") } )

    queue.add(stringRequest)

    }

    fun displayData(data: String) {
        val textView: TextView = findViewById(R.id.text_weather) as TextView
       textView.text = "Test!"
    }

    fun openPreferences(view: View) {
        // open user preferences
        val intent = Intent(this,UserPreferences::class.java).apply {

        }
        startActivity(intent)
    }
}
