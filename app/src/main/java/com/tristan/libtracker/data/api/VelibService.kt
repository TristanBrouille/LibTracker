package com.tristan.libtracker.data.api

import com.tristan.libtracker.data.model.StationInformationResponse
import com.tristan.libtracker.data.model.StationStatusResponse
import retrofit2.http.GET

interface VelibService {
    @GET("station_information.json")
    suspend fun getStationInformation(): StationInformationResponse

    @GET("station_status.json")
    suspend fun getStationStatus(): StationStatusResponse
}
