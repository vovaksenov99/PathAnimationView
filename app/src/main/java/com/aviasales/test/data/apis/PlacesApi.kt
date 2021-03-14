package com.aviasales.test.data.apis

import com.aviasales.test.data.models.Places
import retrofit2.http.POST
import retrofit2.http.Query

interface PlacesApi {

    @POST("autocomplete")
    suspend fun getPlaces(
        @Query("term") term: String,
        @Query("lang") lang: String = "ru"
    ): Places

}
