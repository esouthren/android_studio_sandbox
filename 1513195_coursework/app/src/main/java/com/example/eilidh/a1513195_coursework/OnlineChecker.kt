package com.example.eilidh.a1513195_coursework

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import java.io.IOException
import android.support.v4.content.ContextCompat.getSystemService



class OnlineChecker {

    fun isOnline(context: Context): Boolean {
        Log.i("debug", "checking connectivity...")
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting

    }



    fun displayOfflineError(view: View) {
        // display error message that user is not online
        val builder = AlertDialog.Builder(view.context)

        with(builder)
        {
            setTitle("Connection Error")
            setMessage("It seems you're not connected to the internet. " +
                    "\n\nPlease check your connection and try again!")

            setPositiveButton("OK"){ dialog,which -> null }
            show()
        }
    }



}