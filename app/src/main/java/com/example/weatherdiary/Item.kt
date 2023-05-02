package com.example.weatherdiary

class Item {

    var weatherType : String = ""
    var image : Int = 0

    constructor(name: String, image: Int) {
        this.weatherType = name
        this.image = image
    }

}