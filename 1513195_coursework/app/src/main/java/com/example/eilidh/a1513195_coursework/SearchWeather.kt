package com.example.eilidh.a1513195_coursework

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import javax.security.auth.callback.Callback

class SearchWeather : AppCompatActivity(), Callback {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_weather)
    }
}
