package com.example.api.service

import com.example.api.responseobjects.TripRO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TripClient {
    @GET("experiences")
    fun reposForTrips(
            @Query("city") city: String,
            @Query("format") format: String,
            @Query("detailed") detailed: String,
            @Query("sorting") sorting: String,
            @Query("start_price") start_price: String,
            @Query("end_price") end_price: String,
            @Query("start_date") start_date: String,
            @Query("end_date") end_date: String
    ): Call<TripRO>
}