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
    // array of 0 - 9 to store user preferences
    val PREFERENCES = Array(10, { i -> (i + 1).toString() })
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

    fun setPref(index: Int, value: String) {
        prefs.edit().putString(PREFERENCES[index], value).apply()
    }

    fun getPref(index: Int): String {
        return prefs.getString(PREFERENCES[index], "huh?")
    }
    fun clear() {
        prefs.edit().clear()
        // add 'preferences cleared' toast?
    }
}

class UserPreferences : AppCompatActivity() {

    var prefs: Prefs? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_preferences)
        prefs = Prefs(this)
        var userPrefTextView = arrayOfNulls<String>(10)
        displayUserPreferences()

        // create text view references
        for (i in 0..9) {
            userPrefTextView[i] = "R.id.user_pref_" + i.toString()
        }
    }

    fun displayUserPreferences() {
        // load user preferences and display
        val textView: TextView = findViewById(R.id.user_pref_1) as TextView
        textView.text = prefs!!.getPref(0)
        Log.i("debug", "displaying user preferences" )


    }

    fun updateUserPreferences(view: View) {
        val b: TextView = findViewById(R.id.user_pref_1) as TextView
        prefs!!.setPref(1, b.text.toString())
        displayUserPreferences()
        Log.i("debug", prefs!!.getPref(1))
    }
}