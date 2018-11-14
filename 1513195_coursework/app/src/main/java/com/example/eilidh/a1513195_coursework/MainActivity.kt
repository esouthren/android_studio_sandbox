package com.example.eilidh.a1513195_coursework

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

    }

    fun callApi(view: View) {
        // api gets called here
    }

    fun openPreferences(view: View) {
        // open user preferences
        val intent = Intent(this,UserPreferences::class.java).apply {

        }
        startActivity(intent)
    }
}
