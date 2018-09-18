package com.example.a1513195.myapplication

import android.support.v7.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSubtract: Button = findViewById(R.id.button_subtract)
        val buttonAdd: Button = findViewById(R.id.button_add)

        buttonSubtract.setOnClickListener(this)
        buttonAdd.setOnClickListener(this)

    }

    override fun onClick(v: View) {

        val ratingBar: RatingBar = findViewById(R.id.ratingBar)
        if(v.getId() == R.id.button_add) {
            ratingBar.setRating(ratingBar.getRating() + 0.5f)
        }
        else if(v.getId() === R.id.button_subtract) {
            ratingBar.setRating(ratingBar.getRating() - 0.5f)
        }


    }

}
