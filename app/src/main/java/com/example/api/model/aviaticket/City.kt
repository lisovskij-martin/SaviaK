package com.example.api.model.aviaticket

//поиск городов через API travelpayouts
class City {
    var code: String = ""
    var name: String = ""
    var country_name: String = ""

    override fun toString(): String {
        var result: String = ""
        result= buildString {
            append((if (code!="") "$code " else "") +
                    (if (name!="") "$name " else "") +
                    if (country_name!="") "$country_name" else "")
        }

        return result
    }

}