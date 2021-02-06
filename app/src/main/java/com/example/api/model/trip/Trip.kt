package com.example.api.model.trip

import java.io.Serializable

class Trip : Serializable {
    var id: Int = -1
    var title: String = ""
    var tagline: String = ""
    var url: String = ""
    var max_persons: Int = 0
    var duration: Double = 0.0
    var rating: Double = 0.0
    var guide: Guide = Guide()
    var price: Price = Price()
    var city: City = City()
    var photos: ArrayList<Picture> = ArrayList()

    fun getPhoto(): Picture{
        return this.photos.get(0)
    }

    fun getPriceString(): String {
        return price.value_string
    }

}