package com.example.weatherdiary

import java.sql.Struct

class Entry {
    var date : String
    var location : String
    var temperature : String
    var weather :String
    var desc:String

    constructor(indate:String, inlocation:String, intemperature:String, inweather:String, indesc:String) {
        date = indate
        location = inlocation
        temperature = intemperature
        weather = inweather
        desc = indesc
    }

    override fun toString(): String {
        return date +"-"+location+"-"+temperature+"-"+weather+"-"+desc+"$"
    }
}