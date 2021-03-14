package com.aviasales.test.data.models

import com.google.gson.annotations.SerializedName

data class CityLocation(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double
)