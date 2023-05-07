package com.example.weatherdiary

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class MyViewHolder : RecyclerView.ViewHolder {

    var imageView : ImageView
    var weatherTypeView: TextView

    companion object {
        var WEATHER_TYPE : String = ""
    }
    constructor(itemView : View, activityWeather : View) : super(itemView) {
        imageView = itemView.findViewById(R.id.imageview)
        weatherTypeView = itemView.findViewById(R.id.weatherType)

        itemView.setOnClickListener{
            //store weather type, put in companion object to share to main activity
            WEATHER_TYPE = weatherTypeView.text.toString()

            itemView.setBackgroundColor(Color.parseColor("#FFBB86FC"))
        }
    }

}