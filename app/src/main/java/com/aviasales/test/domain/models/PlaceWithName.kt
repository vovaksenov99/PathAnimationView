package com.aviasales.test.domain.models

import com.aviasales.test.data.models.CityLocation

data class PlaceWithName(
    val location: CityLocation?,
    val airportName: String?,
)
