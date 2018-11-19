package com.example.eilidh.a1513195_coursework

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import android.content.SharedPreferences
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.EditText
import javax.security.auth.callback.Callback


//todo: add delete icon to each option choice



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

class UserPreferences : AppCompatActivity(), Callback {

    var editTextIds = arrayOf(R.id.user_pref_1,
            R.id.user_pref_2,
            R.id.user_pref_3,
            R.id.user_pref_4,
            R.id.user_pref_5,
            R.id.user_pref_6,
            R.id.user_pref_7,
            R.id.user_pref_8,
            R.id.user_pref_9,
            R.id.user_pref_10)

    var prefs: Prefs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_preferences)
        prefs = Prefs(this)
        displayUserPreferences()

    }

    fun displayUserPreferences() {
        // load user preferences and display
        var boxIndex = 0
        for(prefIndex in 0..9) {
            findViewById<EditText>(editTextIds[prefIndex]).visibility = View.INVISIBLE
            val editText: EditText = findViewById<EditText>(editTextIds[boxIndex])
            editText.setText(prefs?.getPrefAddress(prefIndex))

            if(prefs!!.getPrefAddress(prefIndex).length > 1) {
                editText.setText(prefs!!.getPrefAddress(prefIndex))
                editText.visibility = View.VISIBLE
                boxIndex++
            }
        }
        // make box 1 visible always
        findViewById<EditText>(editTextIds[0]).visibility = View.VISIBLE
    }

    fun updateUserPreferences(view: View) {
        for(editIndex in 0..9) {
            val editText: EditText = findViewById<EditText>(editTextIds[editIndex])
            val currentText = editText.text.toString()
            if (currentText.length > 1) {
                getLatLong(currentText, 1, prefs!!)
                prefs?.setPrefAddress(editIndex, editText.text.toString())
            }
        }
        displayUserPreferences()
    }

    fun deletePreference() {
        // update the order of preferences if , say, 2nd preference of 3 is deleted

    }

    fun addPreferenceEditBox(view: View) {
        updateUserPreferences(view)
        for (i in 0..9) {
            val editText: EditText = findViewById<EditText>(editTextIds[i])
                if (prefs!!.getPrefAddress(i).length <= 1) {
                    editText.visibility = View.VISIBLE
                    break
                }
            }
    }

    fun deleteAllPreferences(view: View) {
        prefs!!.clear()
        for(i in 0..9) {
            val editText: EditText = findViewById<EditText>(editTextIds[i])
            editText.setText("")
        }
        displayUserPreferences()
    }

    fun getLatLong(address: String, index: Int, prefs: Prefs) {
        val locationAddress = GeocodingLocation()
        val handler = GeocoderHandler()
        locationAddress.getAddressFromLocation(address,
                applicationContext, handler, index, prefs)
        val meep = handler.rick
        Log.i("debug", "BLORK!!" + meep)

    }
}

// Handler for Geocoding conversions
class GeocoderHandler : Handler() {

    var rick = ""

    override fun handleMessage(message: Message) {
        var locationAddress = ""
        //Log.i("debug", "raw: " + message.data.getString("address"))
        locationAddress = message.getData().getString("address")

        //Log.i("debug", "handlemessage address: $locationAddress")
        if(locationAddress.equals("error")) {
                    Log.i("debug", "Error!")
                    //todo Handle incorrect address - do not add to preferences and display error message to user
                }
        else {
            //Log.i("debug", "handlemessage unsplit address: $locationAddress)")

            //val lat = latLong[0]
            //val long = latLong[1]
            //Log.i("debug", "handlemessage address: $lat")

        }
        rick = locationAddress
        Log.i("debug", rick)
    }
}

