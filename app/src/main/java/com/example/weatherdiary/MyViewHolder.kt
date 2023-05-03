package com.example.weatherdiary

import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyViewHolder : RecyclerView.ViewHolder {

    var imageView : ImageView
    var weatherTypeView: TextView
    var temperature : EditText
    var location : TextView

    constructor(itemView : View, activityWeather : View) : super(itemView) {
        imageView = itemView.findViewById(R.id.imageview)
        weatherTypeView = itemView.findViewById(R.id.weatherType)
        temperature = activityWeather.findViewById(R.id.temperature)
        location = activityWeather.findViewById(R.id.location)


        itemView.setOnClickListener{
            //store weather type, put in companion object to share to main activity
            Log.w("CMSC", "Selected Weather: " + weatherTypeView.text)
            Log.w("CMSC", "Temperature: " + temperature.text.toString() + "°")
            Log.w("CMSC", "Location: " + location.text)
        }
    }

}