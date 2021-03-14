package com.aviasales.test.domain.search

import com.aviasales.test.data.repositories.PlacesRepository
import com.aviasales.test.domain.models.PlaceWithName
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {

    suspend fun getPlaceCoordinatesByName(placeName: String): PlaceWithName {
        val place = placesRepository.getPlaces(placeName).cities.firstOrNull()
        return PlaceWithName(
            place?.location,
            place?.iata?.firstOrNull()
        )
    }


}