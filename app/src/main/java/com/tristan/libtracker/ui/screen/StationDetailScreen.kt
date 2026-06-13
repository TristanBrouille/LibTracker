package com.tristan.libtracker.ui.screen

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tristan.libtracker.ui.theme.ElectricBlue
import com.tristan.libtracker.ui.theme.MechanicalGreen
import com.tristan.libtracker.ui.theme.TransitGreen
import com.tristan.libtracker.ui.viewmodel.StationViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailScreen(
    stationId: Long,
    viewModel: StationViewModel,
    onBack: () -> Unit
) {
    val stations by viewModel.stations.collectAsState()
    val station = stations.find { it.id == stationId }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(station?.name ?: "Détail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        if (station == null) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Station introuvable")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Status", 
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF2F3E46)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        DetailRow(
                            icon = Icons.Default.PedalBike,
                            label = "Mechanical Bikes",
                            value = "${station.mechanicalBikes}",
                            color = MechanicalGreen
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow(
                            icon = Icons.Default.ElectricBike,
                            label = "Electric Bikes",
                            value = "${station.electricBikes}",
                            color = ElectricBlue
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow(
                            icon = Icons.Default.FavoriteBorder, // Just an icon for docks
                            label = "Available Docks",
                            value = "${station.docksAvailable}",
                            color = Color(0xFF2F3E46)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.toggleFavorite(station.id, !station.isFavorite) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (station.isFavorite) Color.White else TransitGreen,
                        contentColor = if (station.isFavorite) TransitGreen else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (station.isFavorite) androidx.compose.foundation.BorderStroke(1.dp, TransitGreen) else null
                ) {
                    Icon(
                        imageVector = if (station.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (station.isFavorite) "Retirer des favoris" else "Mettre en favoris",
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        val gmmIntentUri = "google.navigation:q=${station.lat},${station.lon}&mode=w".toUri()
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = TransitGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TransitGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Itinéraire (Google Maps)",
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "Dernière mise à jour: ${java.text.SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(java.util.Date(station.lastUpdated))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF2F3E46))
        }
        Text(
            text = value, 
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}
