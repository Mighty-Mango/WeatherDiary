package com.example.weatherdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        var recyclerView : RecyclerView = findViewById(R.id.recyclerview)

        var items = arrayListOf<Item>()
        items.add(Item("Sunny", R.drawable.sunny))
        items.add(Item("Sunny With Rain", R.drawable.sunnyrainy))
        items.add(Item("Thunderstorms", R.drawable.thunderstorm))
        items.add(Item("Snow", R.drawable.cloudysnowy))
        items.add(Item("Partly Cloudy", R.drawable.partlycloudy))


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(applicationContext, items)
    }
}