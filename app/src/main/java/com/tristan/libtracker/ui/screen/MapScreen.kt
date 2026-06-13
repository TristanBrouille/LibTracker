package com.tristan.libtracker.ui.screen

import android.content.Intent
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.tristan.libtracker.ui.theme.ElectricBlue
import com.tristan.libtracker.ui.theme.TransitGreen
import com.tristan.libtracker.ui.viewmodel.StationViewModel
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun MapScreen(
    viewModel: StationViewModel,
    onStationClick: (Long) -> Unit,
) {
    val stations by viewModel.stations.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val searchRadius by viewModel.searchRadius.collectAsState()
    val context = LocalContext.current
    
    var searchQuery by remember { mutableStateOf("") }
    var showAllStations by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<com.tristan.libtracker.data.local.StationEntity?>(null) }
    
    val userLoc = userLocation?.let {
        Location("").apply {
            latitude = it.latitude
            longitude = it.longitude
        }
    }

    val stationsWithDistance = stations.map { station ->
        val stationLoc = Location("").apply {
            latitude = station.lat
            longitude = station.lon
        }
        val distance = userLoc?.distanceTo(stationLoc) ?: Float.MAX_VALUE
        station to distance
    }

    val filteredStations = stationsWithDistance.filter { (station, distance) ->
        val matchesSearch = if (searchQuery.isEmpty()) true 
                           else station.name.contains(searchQuery, ignoreCase = true)
        
        val isInRadius = distance <= searchRadius
        
        matchesSearch && (isInRadius || showAllStations)
    }

    val paris = LatLng(48.8566, 2.3522)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(paris, 12f)
    }

    // Center on user location when it becomes available for the first time
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    14f
                )
            )
        }
    }

    val mapProperties = MapProperties(
        isMyLocationEnabled = userLocation != null
    )
    
    val mapUiSettings = MapUiSettings(
        myLocationButtonEnabled = true
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings
        ) {
            userLocation?.let { location ->
                val userLatLng = LatLng(location.latitude, location.longitude)
                Circle(
                    center = userLatLng,
                    radius = searchRadius.toDouble(),
                    fillColor = ElectricBlue.copy(alpha = 0.2f),
                    strokeColor = ElectricBlue,
                    strokeWidth = 2f
                )
            }

            filteredStations.forEach { (station, distance) ->
                val isInRadius = distance <= searchRadius
                val markerAlpha = if (isInRadius) 1.0f else 0.4f

                Marker(
                    state = MarkerState(position = LatLng(station.lat, station.lon)),
                    title = station.name,
                    snippet = "Vélos: ${station.bikesAvailable}, Places: ${station.docksAvailable}",
                    onInfoWindowClick = { onStationClick(station.id) },
                    alpha = markerAlpha,
                    icon = if (!isInRadius) BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) else null,
                    onClick = {
                        selectedStation = station
                        false // Allow default behavior (show info window)
                    }
                )
            }
        }

        selectedStation?.let { station ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        val gmmIntentUri = "google.navigation:q=${station.lat},${station.lon}&mode=w".toUri()
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TransitGreen)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Itinéraire vers ${station.name}", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f), MaterialTheme.shapes.small),
                placeholder = { Text("Rechercher une station...") },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val radiusText = if (searchRadius >= 1000) {
                            "${String.format(Locale.getDefault(), "%.1f", searchRadius / 1000)} km"
                        } else {
                            "${searchRadius.roundToInt()} m"
                        }
                        Text(
                            "Rayon : $radiusText",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Tout voir", style = MaterialTheme.typography.bodySmall)
                            Switch(
                                checked = showAllStations,
                                onCheckedChange = { showAllStations = it },
                                modifier = Modifier.scale(0.7f),
                                thumbContent = {
                                    Icon(
                                        imageVector = if (showAllStations) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        }
                    }
                    Slider(
                        value = searchRadius,
                        onValueChange = { viewModel.setSearchRadius(it) },
                        valueRange = 100f..5000f,
                        colors = SliderDefaults.colors(
                            thumbColor = TransitGreen,
                            activeTrackColor = TransitGreen
                        )
                    )
                }
            }
        }
    }
}
