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
                               context: Context, handler: Handler, prefIndex: Int, prefs: Prefs) {
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
                    val message = Message.obtain()
                    message.target = handler
                    message.what = 1
                    val bundle = Bundle()
                    if (result != null) {

                        bundle.putString("address", result)
                        prefs.setPrefLatLong(prefIndex, result)
                        Log.i("debug", "setting new latlong: " + prefIndex + " \t" + prefs!!.getPrefLatLong(prefIndex))

                    } else {
                        bundle.putString("address", "error")
                    }
                    message.data = bundle
                    message.sendToTarget()


                }
            }
        }
        thread.start()
    }
}