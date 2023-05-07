package com.example.weatherdiary

class Entry {
    var date : String
    var location : String
    var temperature : String
    var weather :String

    constructor(indate:String, inlocation:String, intemperature:String, inweather:String) {
        date = indate
        location = inlocation
        temperature = intemperature
        weather = inweather
    }

    override fun toString(): String {
        return date +","+location+","+temperature+","+weather+"$"
    }
}