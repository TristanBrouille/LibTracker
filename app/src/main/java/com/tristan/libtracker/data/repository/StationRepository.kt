package com.tristan.libtracker.data.repository

import com.tristan.libtracker.data.api.VelibService
import com.tristan.libtracker.data.local.StationDao
import com.tristan.libtracker.data.local.StationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class StationRepository(
    private val velibService: VelibService,
    private val stationDao: StationDao
) {
    val stations: Flow<List<StationEntity>> = stationDao.getAllStations()
    val favorites: Flow<List<StationEntity>> = stationDao.getFavoriteStations()

    suspend fun refreshStations() {
        try {
            val infoResponse = velibService.getStationInformation()
            val statusResponse = velibService.getStationStatus()

            val infoMap = infoResponse.data.stations.associateBy { it.stationId }
            val statusMap = statusResponse.data.stations.associateBy { it.stationId }

            val currentFavorites = stationDao.getFavoriteStations().first().map { it.id }.toSet()

            val entities = infoMap.mapNotNull { (id, info) ->
                statusMap[id]?.let { status ->
                    var mechanical = 0
                    var ebike = 0
                    
                    status.bikeTypes?.forEach { map ->
                        mechanical += map["mechanical"] ?: 0
                        ebike += map["ebike"] ?: 0
                    }
                    
                    StationEntity(
                        id = id,
                        name = info.name,
                        lat = info.lat,
                        lon = info.lon,
                        capacity = info.capacity,
                        bikesAvailable = status.numBikesAvailable,
                        mechanicalBikes = mechanical,
                        electricBikes = ebike,
                        docksAvailable = status.numDocksAvailable,
                        isFavorite = currentFavorites.contains(id),
                        lastUpdated = System.currentTimeMillis()
                    )
                }
            }
            stationDao.insertStations(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun toggleFavorite(stationId: Long, isFavorite: Boolean) {
        stationDao.updateFavoriteStatus(stationId, isFavorite)
    }

    suspend fun getStationById(id: Long): StationEntity? {
        return stationDao.getStationById(id)
    }
}
