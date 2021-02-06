package com.example.api.service

import com.example.api.model.aviaticket.City
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NearestCityClient {
    @GET("nearest_places.json")
    fun reposForGorods(
            @Query("locale") locale: String
    ): Call<List<City>>
}