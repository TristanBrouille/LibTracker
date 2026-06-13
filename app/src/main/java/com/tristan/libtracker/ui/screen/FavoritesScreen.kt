package com.tristan.libtracker.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tristan.libtracker.ui.screen.components.StationItem
import com.tristan.libtracker.ui.viewmodel.StationViewModel

@Composable
fun FavoritesScreen(
    viewModel: StationViewModel,
    onStationClick: (Long) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()

    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aucune station en favoris")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favorites) { station ->
                StationItem(
                    station = station,
                    onStationClick = onStationClick,
                    onToggleFavorite = { viewModel.toggleFavorite(station.id, false) }
                )
            }
        }
    }
}
