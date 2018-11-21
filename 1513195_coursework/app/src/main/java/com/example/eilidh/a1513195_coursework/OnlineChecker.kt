package com.example.eilidh.a1513195_coursework

import android.support.v7.app.AlertDialog
import android.view.View
import java.io.IOException

class OnlineChecker {


    fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }


    fun displayOfflineError(view: View) {
        // display error message that user is not online
        val builder = AlertDialog.Builder(view.context)

        with(builder)
        {
            setTitle("Connection Error")
            setMessage("It seems you're not connected to the internet. " +
                    "\n\nPlease check your connect and try again!")

            setPositiveButton("OK"){ dialog,which -> null }
            show()
        }
    }
}