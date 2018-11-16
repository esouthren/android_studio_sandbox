package com.example.eilidh.a1513195_coursework

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window

import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.widget.TextView

class Prefs (context: Context) {
    val PREFS_FILENAME = "user_preferences"
    val BACKGROUND_COLOR = "background_color"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

    var bgColor: String
        get() = prefs.getString(BACKGROUND_COLOR, "test")
        set(value) = prefs.edit().putString(BACKGROUND_COLOR, value).apply()
}

class UserPreferences : AppCompatActivity() {

    var prefs: Prefs? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_preferences)
        prefs = Prefs(this)
        val bgColor = prefs!!.bgColor
        displayUserPreferences()
    }

    fun displayUserPreferences() {
        // load user preferences and display
        val textView: TextView = findViewById(R.id.user_pref_test) as TextView
        textView.text = prefs!!.bgColor
        Log.i("debug", "displaying user preferences" )


    }

    fun updateUserPreferences(view: View) {
        val b: TextView = findViewById(R.id.input_test) as TextView
        prefs!!.bgColor = b.text.toString()
        displayUserPreferences()
        Log.i("debug", prefs!!.bgColor)
    }
}