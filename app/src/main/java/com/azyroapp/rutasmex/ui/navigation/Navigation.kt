package com.azyroapp.rutasmex.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.azyroapp.rutasmex.ui.screens.HomeScreen
import com.azyroapp.rutasmex.ui.screens.TripDetailScreen
import com.azyroapp.rutasmex.ui.screens.TripHistoryScreen
import com.azyroapp.rutasmex.ui.viewmodel.HomeViewModel

/**
 * Rutas de navegación de la app
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TripHistory : Screen("trip_history")
    object TripDetail : Screen("trip_detail/{tripId}") {
        fun createRoute(tripId: String) = "trip_detail/$tripId"
    }
}

/**
 * Configuración de navegación principal
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel
            )
        }
        
        composable(Screen.TripHistory.route) {
            val trips by viewModel.tripHistory.collectAsState(initial = emptyList())
            
            TripHistoryScreen(
                trips = trips,
                onTripClick = { trip ->
                    navController.navigate(Screen.TripDetail.createRoute(trip.id))
                },
                onDeleteTrip = { trip ->
                    viewModel.deleteTrip(trip)
                },
                onClearHistory = {
                    viewModel.clearTripHistory()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.TripDetail.route,
            arguments = listOf(
                androidx.navigation.navArgument("tripId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: return@composable
            val trips by viewModel.tripHistory.collectAsState(initial = emptyList())
            val trip = trips.find { it.id == tripId }
            
            if (trip != null) {
                TripDetailScreen(
                    trip = trip,
                    onBack = {
                        navController.popBackStack()
                    },
                    onDelete = {
                        viewModel.deleteTrip(trip)
                        navController.popBackStack()
                    }
                )
            } else {
                // Trip no encontrado, volver atrás
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}
