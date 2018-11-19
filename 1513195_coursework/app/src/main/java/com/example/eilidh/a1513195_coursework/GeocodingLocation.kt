package com.example.eilidh.a1513195_coursework


import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log

import java.io.IOException
import java.util.Locale

class GeocodingLocation {

    private val TAG = "GeocodingLocation"


    fun getAddressFromLocation(locationAddress: String,
                               context: Context, prefIndex: Int, prefs: Prefs) {
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

                    } else {
                        // todo what do when lat/long fails
                    }

                }
            }
        }
        thread.start()
    }
}