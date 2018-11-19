package com.example.eilidh.a1513195_coursework

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context)  {
    val PREFS_FILENAME = "user_preferences"
    // array of 0 - 9 to store user preferences
    val PREFERENCES = Array(10, { i -> (i + 1).toString() })
    // stores preferences' lat/long
    val PREFERENCES_LAT_LONG = Array(10, { i -> ((i + 1).toString()+"_LATLONG") })

    val preferences: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

    fun setPrefAddress(index: Int, value: String) {
        this.preferences.edit().putString(PREFERENCES[index], value).apply()
    }

    fun getPrefAddress(index: Int): String {
        return this.preferences.getString(PREFERENCES[index], "")
    }

    fun setPrefLatLong(index: Int, value: String) {
        this.preferences.edit().putString(PREFERENCES_LAT_LONG[index], value).apply()
    }

    fun getPrefLatLong(index: Int): String {
        return this.preferences.getString(PREFERENCES_LAT_LONG[index], "")
    }


    fun clear() {
        for(i in 0..9) {
            this.preferences.edit().putString(PREFERENCES[i], " ").apply()
            this.preferences.edit().putString(PREFERENCES_LAT_LONG[i], " ").apply()
        }
        // add 'preferences cleared' toast?
    }
}