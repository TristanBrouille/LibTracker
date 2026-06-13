package com.tristan.libtracker.ui.screen

sealed class Screen(val route: String, val title: String) {
    data object Login : Screen("login", "Connexion")
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object Map : Screen("map", "Carte")
    data object Favorites : Screen("favorites", "Favoris")
    data object NearMe : Screen("near_me", "À proximité")
    data object Detail : Screen("detail/{stationId}", "Détail") {
        fun createRoute(stationId: Long) = "detail/$stationId"
    }
}
