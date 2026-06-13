package com.tristan.libtracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.tristan.libtracker.ui.theme.LibTrackerTheme
import com.tristan.libtracker.ui.theme.TransitGreen
import com.tristan.libtracker.ui.viewmodel.StationViewModel
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: StationViewModel,
    onNavigateToMap: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToNearMe: () -> Unit
) {
    val stations by viewModel.stations.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val nearStations by viewModel.nearStations.collectAsState()
    val searchRadius by viewModel.searchRadius.collectAsState()

    val radiusText = if (searchRadius >= 1000) {
        "${String.format(Locale.getDefault(), "%.1f", searchRadius / 1000)}km"
    } else {
        "${searchRadius.roundToInt()}m"
    }

    DashboardContent(
        stationCount = stations.size,
        favoriteCount = favorites.size,
        nearStationCount = nearStations.size,
        radiusText = radiusText,
        onRefresh = { viewModel.refresh() },
        onNavigateToMap = onNavigateToMap,
        onNavigateToFavorites = onNavigateToFavorites,
        onNavigateToNearMe = onNavigateToNearMe
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    stationCount: Int,
    favoriteCount: Int,
    nearStationCount: Int,
    radiusText: String,
    onRefresh: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToNearMe: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "libTracker", 
                        fontWeight = FontWeight.Bold,
                        color = TransitGreen
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Overview - Central Paris",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F3E46)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    title = "Stations",
                    count = stationCount.toString(),
                    icon = Icons.Default.Map,
                    onClick = onNavigateToMap
                )
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    title = "Favoris",
                    count = favoriteCount.toString(),
                    icon = Icons.Default.Favorite,
                    onClick = onNavigateToFavorites
                )
            }

            DashboardCard(
                modifier = Modifier.fillMaxWidth(),
                title = "À proximité ($radiusText)",
                count = "$nearStationCount stations",
                icon = Icons.Default.NearMe,
                onClick = onNavigateToNearMe
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TransitGreen)
            ) {
                Text("Rafraîchir les données", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(140.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = TransitGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title, 
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2F3E46).copy(alpha = 0.6f)
            )
            Text(
                text = count, 
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF2F3E46)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    LibTrackerTheme {
        DashboardContent(
            stationCount = 1450,
            favoriteCount = 12,
            nearStationCount = 5,
            radiusText = "1.0km",
            onRefresh = {},
            onNavigateToMap = {},
            onNavigateToFavorites = {},
            onNavigateToNearMe = {}
        )
    }
}
