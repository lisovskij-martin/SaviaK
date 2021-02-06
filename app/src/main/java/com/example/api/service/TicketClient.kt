package com.example.api.service

import com.example.api.responseobjects.TicketRO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketClient {
    @GET("price_matrix")
    fun reposForTickets(
        @Query("origin_iata") origin_iata: String,
        @Query("destination_iata") destination_iata: String,
        @Query("depart_start") depart_start: String?,
        @Query("currency") currency: String,
        @Query("depart_range") depart_range: String,
        @Query("affiliate") affiliate: String
    ): Call<TicketRO>
}