package com.example.api.model.trip

import java.io.Serializable

// для поиска городов через API tripster
class City : Serializable {
    var name_ru: String=""
    var name_en: String=""
    var id: String=""
    var iata: String=""
    var country: Country= Country()
}