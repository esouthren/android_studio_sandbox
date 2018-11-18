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
import android.widget.EditText
import android.widget.TextView
import com.example.eilidh.a1513195_coursework.R.id.none



class Prefs (context: Context) {
    val PREFS_FILENAME = "user_preferences"
    // array of 0 - 9 to store user preferences
    val PREFERENCES = Array(10, { i -> (i + 1).toString() })

    val preferences: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

    fun setPref(index: Int, value: String) {
        this.preferences.edit().putString(PREFERENCES[index], value).apply()
    }

    fun getPref(index: Int): String {
        return this.preferences.getString(PREFERENCES[index], "")

    }
    fun clear() {
        for(i in 0..9) {
            this.preferences.edit().putString(PREFERENCES[i], " ").apply()
        }
        // add 'preferences cleared' toast?
    }

}

class UserPreferences : AppCompatActivity() {

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
            Log.i("debug", "pref " + prefIndex + ": " + prefs!!.getPref(prefIndex).length.toString() )
            val editText: EditText = findViewById<EditText>(editTextIds[boxIndex])
            editText.setText(prefs?.getPref(prefIndex))
            if(prefs!!.getPref(prefIndex).length > 1) {
                //Log.i("debug", "contains text!" + prefs!!.getPref(i))
                editText.setText(prefs!!.getPref(prefIndex))
                Log.i("debug", "setting visible")
                editText.visibility = View.VISIBLE
                boxIndex++
            }


        }
        // make box 1 visible always
        findViewById<EditText>(editTextIds[0]).visibility = View.VISIBLE



        //editText.text = prefs!!.getPref(0)
        Log.i("debug", "displaying user preferences" )
        //removeUnusedEditTexts()

    }

    fun updateUserPreferences(view: View) {


        for(editIndex in 0..9) {
            val editText: EditText = findViewById<EditText>(editTextIds[editIndex])
            if (editText.text.toString().length > 1) {
                Log.i("debug", "value of textbox: " + editText.text.toString().length.toString())
                prefs?.setPref(editIndex, editText.text.toString())
                Log.i("debug", "setting preference: " + editIndex + " - " + prefs!!.getPref(editIndex))
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
                if (prefs!!.getPref(i).length <= 1) {
                    editText.visibility = View.VISIBLE
                    break
                }
            }
    }

    fun deleteAllPreferences(view: View) {
        Log.i("debug", "Deleting all preferences")
        prefs!!.clear()
        for(i in 0..9) {
            val editText: EditText = findViewById<EditText>(editTextIds[i])
            editText.setText("")
            Log.i("debug", "deleted pref: " + prefs!!.getPref(i))
        }
        displayUserPreferences()
    }
}