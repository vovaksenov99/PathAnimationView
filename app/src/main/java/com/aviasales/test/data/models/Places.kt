package com.aviasales.test.data.models

import com.google.gson.annotations.SerializedName

data class Places(
    @SerializedName("cities")
    val cities: List<City>
)

