package com.siti.cosmos.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.siti.cosmos.ui.screens.*
import com.siti.cosmos.ui.viewmodel.FavoritesViewModel
import com.siti.cosmos.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    startDestination: String = NavRoutes.SPLASH,
    onThemeChange: (String) -> Unit
) {
    val navController = rememberNavController()
    val navBackStack  by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStack?.destination?.route

    // Tabs shown in bottom bar
    data class BottomTab(val route: String, val icon: String, val label: String)
    val bottomTabs = listOf(
        BottomTab(NavRoutes.HOME,      "⌂",  "Home"),
        BottomTab(NavRoutes.APOD,      "🌠", "APOD"),
        BottomTab(NavRoutes.MARS,      "🔴", "Mars"),
        BottomTab(NavRoutes.NEO,       "☄️", "NEO"),
        BottomTab(NavRoutes.FAVORITES, "⭐", "Saved"),
    )
    val showBottomBar = currentRoute in bottomTabs.map { it.route }

    // Shared ViewModels — survive navigation
    val favVm: FavoritesViewModel = viewModel()
    val settingsVm: SettingsViewModel = viewModel()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomTabs.forEach { tab ->
                        val selected = navBackStack?.destination
                            ?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick  = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = { Text(tab.icon, fontSize = 20.sp) },
                            label = {
                                Text(tab.label, fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = MaterialTheme.colorScheme.primary,
                                selectedTextColor   = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding),
            enterTransition  = {
                fadeIn(tween(220)) + slideInHorizontally(tween(220)) { it / 8 }
            },
            exitTransition   = { fadeOut(tween(180)) },
            popEnterTransition  = {
                fadeIn(tween(220)) + slideInHorizontally(tween(220)) { -it / 8 }
            },
            popExitTransition   = {
                fadeOut(tween(180)) + slideOutHorizontally(tween(220)) { it / 8 }
            }
        ) {
            // Splash
            composable(NavRoutes.SPLASH) {
                SplashScreen(onFinished = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                })
            }

            // Home
            composable(NavRoutes.HOME) {
                HomeScreen(onNavigate = { route -> navController.navigate(route) })
            }

            // APOD
            composable(NavRoutes.APOD) {
                ApodScreen(onBack = { navController.popBackStack() }, favVm = favVm)
            }

            // Mars
            composable(NavRoutes.MARS) {
                MarsScreen(onBack = { navController.popBackStack() }, favVm = favVm)
            }

            // NEO
            composable(NavRoutes.NEO) {
                NeoScreen(onBack = { navController.popBackStack() })
            }

            // Favorites
            composable(NavRoutes.FAVORITES) {
                FavoritesScreen(onBack = { navController.popBackStack() }, favVm = favVm)
            }

            // Settings (accessed from Home header & its own route)
            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    onBack        = { navController.popBackStack() },
                    vm            = settingsVm,
                    onThemeChange = onThemeChange
                )
            }

            // Planets list
            composable(NavRoutes.PLANETS) {
                PlanetsScreen(
                    onBack        = { navController.popBackStack() },
                    onPlanetClick = { name ->
                        navController.navigate(NavRoutes.planetDetail(name))
                    }
                )
            }

            // Planet detail (with 3D planet)
            composable(
                route     = NavRoutes.PLANET_DETAIL,
                arguments = listOf(navArgument("planetName") { type = NavType.StringType })
            ) { backStackEntry ->
                val planetName = backStackEntry.arguments?.getString("planetName") ?: "Mars"
                PlanetDetailScreen(
                    planetName = planetName,
                    onBack     = { navController.popBackStack() }
                )
            }
        }
    }
}
