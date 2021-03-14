package com.aviasales.test.data.models

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("location")
    val location: CityLocation,
    @SerializedName("iata")
    val iata: List<String>
)