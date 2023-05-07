package com.example.weatherdiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var date = findViewById(R.id.date_in) as EditText
        var loc = findViewById(R.id.loc_in) as EditText
        var temp = findViewById(R.id.temp_in) as EditText
        var weather = findViewById(R.id.weather_in) as EditText
        val submit_btn = findViewById(R.id.submit_btn) as Button
        //val btn_click_me = findViewById(R.id.button_id) as Button

        submit_btn.setOnClickListener {
            var dateIn = date.text.toString()
            var locIn = loc.text.toString()
            var weatherIn = weather.text.toString()
            var tempIn = temp.text.toString()
            var newEnt = Entry(dateIn, locIn, tempIn, weatherIn)
            entry = newEnt
            var myIntent: Intent = Intent(this, Entries::class.java)
            startActivity(myIntent)
        }

       // btn_click_me.setOnClickListener {
         //   var myIntent: Intent = Intent(this, Entries::class.java)
           // startActivity(myIntent)
        //}
    }
    companion object {
        lateinit var entry : Entry
    }
}
