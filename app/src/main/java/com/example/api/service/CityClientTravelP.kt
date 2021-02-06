package com.example.api.service

import com.example.api.model.aviaticket.City
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//travelpayouts
interface CityClientTravelP {
    @GET("places.json")
    fun reposForCities(
            @Query("term") term: String,
            @Query("locale") locale: String,
            @Query("types[]") city: String,
            @Query("max") max: String
    ): Call<List<City>>
}