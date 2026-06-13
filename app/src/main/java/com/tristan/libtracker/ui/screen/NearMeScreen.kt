package com.tristan.libtracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tristan.libtracker.data.local.StationEntity
import com.tristan.libtracker.ui.screen.components.StationItem
import com.tristan.libtracker.ui.viewmodel.StationViewModel
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun NearMeScreen(
    viewModel: StationViewModel,
    onStationClick: (Long) -> Unit
) {
    val nearStationsWithDistance by viewModel.nearStations.collectAsState()
    val searchRadius by viewModel.searchRadius.collectAsState()
    val radiusText = if (searchRadius >= 1000) {
        "${String.format(Locale.getDefault(), "%.1f", searchRadius / 1000)}km"
    } else {
        "${searchRadius.roundToInt()}m"
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Stations à moins de $radiusText",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        
        if (nearStationsWithDistance.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Aucune station à moins $radiusText")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.updateLocation() }) {
                        Text("Mettre à jour ma position")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(nearStationsWithDistance) { (station, distance) ->
                    NearStationItem(
                        station = station,
                        distance = distance,
                        onStationClick = onStationClick,
                        onToggleFavorite = { viewModel.toggleFavorite(station.id, !station.isFavorite) }
                    )
                }
            }
        }
    }
}

@Composable
fun NearStationItem(
    station: StationEntity,
    distance: Float,
    onStationClick: (Long) -> Unit,
    onToggleFavorite: () -> Unit
) {
    val distanceText = if (distance >= 1000) {
        String.format(Locale.getDefault(), "%.1fkm", distance / 1000)
    } else {
        String.format(Locale.getDefault(), "%.0fm", distance)
    }
    StationItem(
        station = station,
        onStationClick = onStationClick,
        onToggleFavorite = onToggleFavorite,
        distanceText = distanceText
    )
}
