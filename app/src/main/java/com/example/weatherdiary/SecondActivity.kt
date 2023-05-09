package com.example.weatherdiary

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Date

class SecondActivity : AppCompatActivity() {

    private lateinit var datePickerDialog : DatePickerDialog
    private lateinit var dateButton : TextView
    private lateinit var location : TextView

    private var tempString = "INVALID"
    private var dateString = "INVALID"
    private var locString = "INVALID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        initDatePicker()
        dateButton = findViewById(R.id.selectedDateButton)
        dateButton.text = getTodaysDate()

        location = findViewById(R.id.location)
        location.text = MapsActivity.CITY_STATE

        var loc:String = intent.extras?.getString("location") ?: "No location found"
        Log.w("SecondActivity", "Location is: " + loc)

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

    private fun getTodaysDate(): String {
        var cal : Calendar = Calendar.getInstance()
        var year : Int = cal.get(Calendar.YEAR)
        var month : Int = cal.get(Calendar.MONTH)
        var day : Int = cal.get(Calendar.DAY_OF_MONTH)

        return makeDateString(day, month, year)
    }

    private fun initDatePicker() {
        var dateSetListener : DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener(){

                datePicker: DatePicker, year: Int, month: Int, day: Int ->
            var date : String = makeDateString(day, month, year)
            dateButton.text = date
        }

        var cal : Calendar = Calendar.getInstance()
        var year : Int = cal.get(Calendar.YEAR)
        var month : Int = cal.get(Calendar.MONTH)
        var day : Int = cal.get(Calendar.DAY_OF_MONTH)

        var style : Int = AlertDialog.BUTTON_POSITIVE

        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)

    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return monthFormat(month) + " " + day + " " + year
    }

    private fun monthFormat(month : Int) : String {
        if(month == 0)
            return "JAN"
        if(month == 1)
            return "FEB"
        if(month == 2)
            return "MAR"
        if(month == 3)
            return "APR"
        if(month == 4)
            return "MAY"
        if(month == 5)
            return "JUN"
        if(month == 6)
            return "JUL"
        if(month == 7)
            return "AUG"
        if(month == 8)
            return "SEP"
        if(month == 9)
            return "OCT"
        if(month == 10)
            return "NOC"
        if(month == 11)
            return "DEC"
        return "JAN"
    }

    public fun datePicker (view : View) {
        datePickerDialog.show()
    }

    fun saveItems(view : View){
        location = findViewById(R.id.location)
        val temperature : EditText = findViewById(R.id.temperature)
        val date : TextView = findViewById(R.id.selectedDateButton)
        val weatherType = MyViewHolder.WEATHER_TYPE
        var valid : Boolean = true

        if(temperature.text.isEmpty() || weatherType.isEmpty()){
            val initToast : Toast = Toast.makeText(this, "Please Submit Something For All Fields", Toast.LENGTH_LONG)
            initToast.show()
            return
        }
        dateString = date.text.toString()
        locString = location.text.toString()
        tempString = temperature.text.toString()
        Log.v("entry",dateString)
        Log.v("entry",locString)
        Log.v("entry",tempString)
        Log.v("entry",weatherType)
        entry = Entry(dateString,
            locString,
            tempString,
            weatherType,"description")

        if(valid){
            val myIntent : Intent = Intent( this, Entries::class.java )
            startActivity( myIntent )
            overridePendingTransition(R.anim.anim_one,R.anim.anim_two)
        }
    }

    companion object {
        lateinit var entry : Entry
    }
}