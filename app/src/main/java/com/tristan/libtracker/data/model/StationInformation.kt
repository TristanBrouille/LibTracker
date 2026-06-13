package com.tristan.libtracker.data.model

import com.google.gson.annotations.SerializedName

data class StationInformationResponse(
    @SerializedName("data") val data: StationInformationData
)

data class StationInformationData(
    @SerializedName("stations") val stations: List<StationInformation>
)

data class StationInformation(
    @SerializedName("station_id") val stationId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("capacity") val capacity: Int
)
