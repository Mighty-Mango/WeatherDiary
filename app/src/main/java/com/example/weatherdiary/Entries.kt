package com.example.weatherdiary

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

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


        //for ads
        var adView: AdView = AdView(this)
        var adSize:AdSize = AdSize(AdSize.FULL_WIDTH,AdSize.AUTO_HEIGHT)
        adView.setAdSize(adSize)
        var adUnitId : String = "ca-app-pub-3940256099942544/6300978111"
        adView.adUnitId = adUnitId

        var builder: AdRequest.Builder = AdRequest.Builder()
        builder.addKeyword("workout")
        builder.addKeyword("gaming")
        var request : AdRequest = builder.build()

        var adLayout : LinearLayout = findViewById<LinearLayout>(R.id.ad_view)
        adLayout.addView(adView)

        try{
            adView.loadAd(request)
        } catch(e:Exception){
            Log.w("Entries","Ad failed to load")
        }

        //unpacking
        if (pastEntries!=null) {
            var lis = pastEntries.split("$")
            println(lis)
            for (i in lis) {
                var reslis = i.split("-")
                if (reslis.size >1) {
                    if (reslis.size == 5) {
                        var n = Entry(reslis[0],reslis[1],reslis[2],reslis[3],reslis[4])
                        entriesList.add(n)
                    }
                    else {
                        var n = Entry(reslis[0],reslis[1],reslis[2],reslis[3], "notsaved")
                        entriesList.add(n)
                    }


                }
            }
        }
        try {
            var newEntry = SecondActivity.entry
            entriesList.add(newEntry)
        } catch (e:Exception) {
            Log.w("tag","from view 1")
        }

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