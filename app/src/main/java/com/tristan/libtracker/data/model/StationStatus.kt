package com.tristan.libtracker.data.model

import com.google.gson.annotations.SerializedName

data class StationStatusResponse(
    @SerializedName("data") val data: StationStatusData
)

data class StationStatusData(
    @SerializedName("stations") val stations: List<StationStatus>
)

data class StationStatus(
    @SerializedName("station_id") val stationId: Long,
    @SerializedName("num_bikes_available") val numBikesAvailable: Int,
    @SerializedName("num_docks_available") val numDocksAvailable: Int,
    @SerializedName("is_installed") val isInstalled: Int,
    @SerializedName("is_renting") val isRenting: Int,
    @SerializedName("is_returning") val isReturning: Int,
    @SerializedName("num_bikes_available_types") val bikeTypes: List<Map<String, Int>>?
)
