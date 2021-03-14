package com.aviasales.test.data.repositories

import com.aviasales.test.data.apis.PlacesApi
import javax.inject.Inject

class PlacesRepository @Inject constructor(
    private val placesApi: PlacesApi
) {

    suspend fun getPlaces(term: String) = placesApi.getPlaces(term)

}