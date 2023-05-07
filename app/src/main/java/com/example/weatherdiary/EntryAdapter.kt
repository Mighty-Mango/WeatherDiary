package com.example.weatherdiary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class EntryAdapter():BaseAdapter() {
    var context: Context? = null
    lateinit var entriesList : ArrayList<Entry>
    var inflater: LayoutInflater? =null

    constructor(contextIn:Context, listIn:ArrayList<Entry>) : this() {
        context = contextIn
        entriesList = listIn
        inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
    override fun getCount(): Int {
       return entriesList.size
    }

    override fun getItem(p0: Int): Any {
        return entriesList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, parent: ViewGroup): View {
        val rowView = inflater?.inflate(R.layout.list_item, parent, false)
        val dateTextView = rowView?.findViewById(R.id.date) as TextView

        val locationTextView = rowView.findViewById(R.id.location) as TextView
        val temperatureTextView = rowView.findViewById(R.id.temperature) as TextView
        val thumbnailImageView = rowView.findViewById(R.id.list_thumbnail) as ImageView
        val entry = getItem(p0) as Entry
        dateTextView.text = entry.date
        locationTextView.text = entry.location
        temperatureTextView.text = entry.temperature.toString()
        return rowView
    }

}