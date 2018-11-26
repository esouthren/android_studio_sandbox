package com.example.eilidh.a1513195_coursework


import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View

import java.io.IOException
import java.util.Locale

class GeocodingLocation(activity: Activity) {

    private val TAG = "GeocodingLocation"
    private var activity = activity


    fun getAddressFromLocation(locationAddress: String,
                               context: Context, prefIndex: Int, prefs: UserPreferences, view: View) {
        Log.i("debug", "Starting to find lat/long...")
        val thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList = geocoder.getFromLocationName(locationAddress, 1)
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0]
                        val sb = StringBuilder()
                        sb.append(address.latitude).append("\n")
                        sb.append(address.longitude).append("\n")
                        result = sb.toString()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Unable to connect to Geocoder", e)
                } finally {

                    if (result != null) {
                        // set preference
                        prefs.setPrefLatLong(prefIndex, result)
                        Log.i("debug", "setting new latlong: " + prefIndex + " \t" + prefs!!.getPrefLatLong(prefIndex))

                    }
                }
            }
        }
        thread.start()
    }

    fun invalidAddressAlert(view: View, address: String) {

        val builder = AlertDialog.Builder(view.context)
        with(builder)
        {
            setTitle("Oh no!")
            setMessage("We couldn't process your address: " + address + "\nPlease try again with a different address.")
            setPositiveButton("OK") { dialog, which -> null }
            show()
        }
    }
}