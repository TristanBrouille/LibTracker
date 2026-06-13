package com.tristan.libtracker.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tristan.libtracker.data.local.StationEntity
import com.tristan.libtracker.data.location.LocationClient
import com.tristan.libtracker.data.repository.StationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StationViewModel(
    private val repository: StationRepository,
    private val locationClient: LocationClient
) : ViewModel() {

    val stations: StateFlow<List<StationEntity>> = repository.stations.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val favorites: StateFlow<List<StationEntity>> = repository.favorites.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation

    private val _searchRadius = MutableStateFlow(1000f) // Default 1km
    val searchRadius: StateFlow<Float> = _searchRadius

    val nearStations: StateFlow<List<Pair<StationEntity, Float>>> = combine(
        stations,
        _userLocation,
        _searchRadius
    ) { stations, location, radius ->
        if (location == null) emptyList()
        else {
            stations.map { station ->
                val stationLocation = Location("").apply {
                    latitude = station.lat
                    longitude = station.lon
                }
                station to location.distanceTo(stationLocation)
            }.filter { it.second < radius }
                .sortedBy { it.second }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refresh()
        updateLocation()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshStations()
        }
    }

    fun updateLocation() {
        viewModelScope.launch {
            _userLocation.value = locationClient.getCurrentLocation()
        }
    }

    fun setSearchRadius(radius: Float) {
        _searchRadius.value = radius
    }

    fun toggleFavorite(stationId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(stationId, isFavorite)
        }
    }

    class Factory(
        private val repository: StationRepository,
        private val locationClient: LocationClient
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StationViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StationViewModel(repository, locationClient) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
