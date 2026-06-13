package com.tristan.libtracker.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.tristan.libtracker.ui.viewmodel.StationViewModel

@Composable
fun MainScreen(viewModel: StationViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Detail.route && 
                currentRoute != Screen.Login.route && 
                currentRoute != Screen.Dashboard.route) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Place, contentDescription = null) },
                        label = { Text(Screen.Map.title) },
                        selected = currentRoute == Screen.Map.route,
                        onClick = { navController.navigate(Screen.Map.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text(Screen.Favorites.title) },
                        selected = currentRoute == Screen.Favorites.route,
                        onClick = { navController.navigate(Screen.Favorites.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        label = { Text(Screen.NearMe.title) },
                        selected = currentRoute == Screen.NearMe.route,
                        onClick = { navController.navigate(Screen.NearMe.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        } }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToMap = { navController.navigate(Screen.Map.route) },
                    onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                    onNavigateToNearMe = { navController.navigate(Screen.NearMe.route) }
                )
            }
            composable(Screen.Map.route) {
                MapScreen(viewModel) { stationId ->
                    navController.navigate(Screen.Detail.createRoute(stationId))
                }
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(viewModel) { stationId ->
                    navController.navigate(Screen.Detail.createRoute(stationId))
                }
            }
            composable(Screen.NearMe.route) {
                NearMeScreen(viewModel) { stationId ->
                    navController.navigate(Screen.Detail.createRoute(stationId))
                }
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("stationId") { type = NavType.LongType })
            ) { backStackEntry ->
                val stationId = backStackEntry.arguments?.getLong("stationId") ?: return@composable
                StationDetailScreen(stationId, viewModel) {
                    navController.popBackStack()
                }
            }
        }
    }
}
