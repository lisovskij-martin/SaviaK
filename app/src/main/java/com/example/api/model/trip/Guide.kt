package com.example.api.model.trip

import java.io.Serializable

class Guide () : Serializable {
    var first_name: String=""
    var id: String=""
    var avatar: Avatar= Avatar()
    var url: String=""
    var rating: Double=(0).toDouble()
    constructor (first_name: String, id: String, avatar: Avatar, url: String, rating: Double) : this(){
        this.first_name=first_name
        this.id=id
        this.avatar=avatar
        this.url=url
        this.rating=rating
    }

}