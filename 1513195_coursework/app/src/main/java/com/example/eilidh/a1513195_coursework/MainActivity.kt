package com.example.eilidh.a1513195_coursework

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

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

        mDb = WeatherDatabase.getInstance(this)
        fb = FillDatabase(mDb!!, mDbWorkerThread, prefs!!, this@MainActivity, applicationContext)

        fb.displayDbData(this@MainActivity)
        prefs!!.setCurrentPrefView(0)
    }

    fun changePreferenceViewLeft(view: View) {
        changePreferenceView("left")
    }

    fun changePreferenceViewRight(view: View) {
        changePreferenceView("right")
    }

    fun updateWeatherData(view: View) {
        fb.updateWeatherData(view)
    }

    fun changePreferenceView(direction: String) {
        // move the view to another user preference
        val numberOfFilledPreferences = prefs!!.getNumberOfPreferences()
        val currentPrefView = prefs!!.getCurrentPrefView()

        // user clicks 'left' button
        if(direction.equals("left")) {
            if(currentPrefView>0) {
                    prefs!!.setCurrentPrefView((currentPrefView - 1))
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
                fb.getPreferenceWeather(prefs!!.getPrefAddress(currentPrefView), this@MainActivity)

            } else {
                // circle back to first preference
                prefs!!.setCurrentPrefView(0)
            }
        }
        // finally, update screen to reflect new selection
        fb.getPreferenceWeather(prefs!!.getPrefAddress(currentPrefView), this@MainActivity)

    }

    fun openPreferences(view: View) {
        // open user preferences activity
        val intent = Intent(this, SetUserPreferences::class.java).apply {
        }
        startActivity(intent)
    }

    fun openSearch(view: View) {
        // open search feature
        val intent = Intent(this, SearchWeather::class.java).apply {
        }
        startActivity(intent)
    }
}
