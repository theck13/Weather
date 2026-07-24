package com.heckofanapp.weather.core.network.sources.weather.accu.json

import com.google.gson.annotations.SerializedName

data class AccuLocationJson(
    @SerializedName("Key") val key: String,
)
