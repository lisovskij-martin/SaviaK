package com.example.api.model.trip

import java.io.Serializable

class Price() : Serializable {
    var value: Double = 0.0
    var currency: String = ""
    var price_from: String = ""
    var unit_string: String = ""
    var value_string: String = ""
}