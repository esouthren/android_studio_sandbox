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


class SetUserPreferences : AppCompatActivity(), Callback {

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
                Log.i("debug", "display prefs: " + prefs!!.getPrefAddress(prefIndex))

                editText.setText(prefs!!.getPrefAddress(prefIndex))
                editText.visibility = View.VISIBLE
                boxIndex++
            }
        }
        // make box 1 visible always
        findViewById<EditText>(editTextIds[0]).visibility = View.VISIBLE
    }

    fun updatePreferences(view: View) {
        for(editIndex in 0..9) {
            val editText: EditText = findViewById<EditText>(editTextIds[editIndex])
            val currentText = editText.text.toString()
            if (currentText.length > 1) {
                Log.i("debug", "found a value!")
                prefs!!.setPrefAddress(editIndex, currentText)
                // latlong is set from within thread
                getLatLong(currentText, editIndex, prefs!!)
            } else {
                prefs!!.setPrefAddress(editIndex, "")
                prefs!!.setPrefLatLong(editIndex, "")
            }
        }
        //displayUserPreferences()
    }

    fun deletePreference() {
        // update the order of preferences if , say, 2nd preference of 3 is deleted
    }

    fun addPreferenceEditBox(view: View) {
        Log.i("debug", "Adding preference box")
        //updatePreferences(view)
        for (i in 0..9) {
            val editText: EditText = findViewById<EditText>(editTextIds[i])
                if (editText.text.toString().length <= 1) {
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
            prefs!!.setPrefLatLong(i, "")
        }
        displayUserPreferences()
    }

    fun getLatLong(address: String, index: Int, prefs: Prefs) {
        val locationAddress = GeocodingLocation()
        locationAddress.getAddressFromLocation(address,
                applicationContext, index, prefs)
    }
}
