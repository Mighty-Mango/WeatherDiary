package com.example.weatherdiary

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyViewHolder : RecyclerView.ViewHolder {

    var imageView : ImageView
    var weatherTypeView: TextView
    var temperature : TextView
    var location : TextView

    constructor(itemView : View, activityWeather : View) : super(itemView) {
        imageView = itemView.findViewById(R.id.imageview)
        weatherTypeView = itemView.findViewById(R.id.weatherType)
        temperature : Text


        itemView.setOnClickListener{
            Log.w("CMSC", "Selected Weather: " + weatherTypeView.text)
        }
    }

}