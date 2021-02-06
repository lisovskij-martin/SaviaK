package com.example.api.service

import com.example.api.responseobjects.CityRO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//tripster
interface CityClientTripster {
    @GET("cities")
    fun reposForCities(
        @Query("iata") iata: String
    ): Call<CityRO>
}
