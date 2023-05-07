package com.example.weatherdiary

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class Entries : AppCompatActivity() {
    var entriesList = ArrayList<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entries)

        val btn_click_me = findViewById(R.id.button_id) as Button
        val clear_btn = findViewById(R.id.button_clear) as Button
        val lv = findViewById(R.id.list) as ListView
        var pref : SharedPreferences =
            this.getSharedPreferences( this.packageName + "_preferences",
                Context.MODE_PRIVATE )
        var editor : SharedPreferences.Editor = pref.edit()
        var pastEntries=( pref.getString("pastEntries","") )
        //unpacking
        if (pastEntries!=null) {
            var lis = pastEntries.split("$")
            println(lis)
            for (i in lis) {
                var reslis = i.split(",")
                //print(reslis)
                if (reslis.size >1) {
                    var n = Entry(reslis[0],reslis[1],reslis[2],reslis[3])
                    entriesList.add(n)
                }
            }
        }
        //var newEntry =MainActivity.entry
        //entriesList.add(newEntry)
        // write this entries to data
        var entriesSave =""
        for (entry in entriesList) {
            entriesSave=entriesSave+entry.toString()
        }
        editor.putString("pastEntries",entriesSave)
        editor.commit()
        //keep for testing if the custom adapter breaks
        //val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entriesList)
        val adapter = EntryAdapter(this, entriesList)
        lv.adapter = adapter
        clear_btn.setOnClickListener {
            editor.putString("pastEntries",null)
            editor.commit()
            this.finish()
        }

        btn_click_me.setOnClickListener {
            this.finish()
        }
    }
}