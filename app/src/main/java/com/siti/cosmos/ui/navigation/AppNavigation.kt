package com.siti.cosmos.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.siti.cosmos.R
import com.siti.cosmos.R
import com.siti.cosmos.ui.screens.*
import com.siti.cosmos.ui.theme.*
import com.siti.cosmos.ui.viewmodel.FavoritesViewModel
import com.siti.cosmos.ui.viewmodel.SettingsViewModel

// ── Bottom nav tab definition ──────────────────────────────────────────────────
private data class BottomTab(
    val route: String,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector,
    val labelRes: Int          // string resource ID — resolved at composable time
)

private val bottomTabs = listOf(
    BottomTab(NavRoutes.HOME,      Icons.Outlined.Home,            Icons.Filled.Home,            R.string.nav_label_home),
    BottomTab(NavRoutes.APOD,      Icons.Outlined.PhotoCamera,     Icons.Outlined.PhotoCamera,   R.string.nav_label_apod),
    BottomTab(NavRoutes.MARS,      Icons.Outlined.Explore,         Icons.Outlined.Explore,       R.string.nav_label_mars),
    BottomTab(NavRoutes.NEO,       Icons.Outlined.Stars,           Icons.Outlined.Stars,         R.string.nav_label_neo),
    BottomTab(NavRoutes.FAVORITES, Icons.Outlined.FavoriteBorder,  Icons.Filled.Favorite,        R.string.nav_label_favs),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    startDestination: String = NavRoutes.SPLASH,
    onThemeChange: (String) -> Unit
) {
    val navController = rememberNavController()
    val navBackStack  by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStack?.destination?.route

    val showBottomBar = currentRoute in bottomTabs.map { it.route }

    // Shared ViewModels — survive navigation
    val favVm: FavoritesViewModel = viewModel()
    val settingsVm: SettingsViewModel = viewModel()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                CosmosBottomNav(
                    tabs          = bottomTabs,
                    navBackStack  = navBackStack,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding),
            enterTransition  = {
                fadeIn(tween(260, easing = FastOutSlowInEasing)) +
                        slideInHorizontally(tween(260, easing = FastOutSlowInEasing)) { it / 10 }
            },
            exitTransition   = {
                fadeOut(tween(200, easing = FastOutSlowInEasing))
            },
            popEnterTransition = {
                fadeIn(tween(260, easing = FastOutSlowInEasing)) +
                        slideInHorizontally(tween(260, easing = FastOutSlowInEasing)) { -it / 10 }
            },
            popExitTransition = {
                fadeOut(tween(200, easing = FastOutSlowInEasing)) +
                        slideOutHorizontally(tween(260, easing = FastOutSlowInEasing)) { it / 10 }
            }
        ) {
            composable(NavRoutes.SPLASH) {
                SplashScreen(onFinished = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(NavRoutes.HOME) {
                HomeScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(NavRoutes.APOD) {
                ApodScreen(onBack = { navController.popBackStack() }, favVm = favVm)
            }
            composable(NavRoutes.MARS) {
                MarsScreen(onBack = { navController.popBackStack() }, favVm = favVm)
            }
            composable(NavRoutes.NEO) {
                NeoScreen(onBack = { navController.popBackStack() })
            }
            composable(NavRoutes.FAVORITES) {
                FavoritesScreen(onBack = { navController.popBackStack() }, favVm = favVm)
            }
            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    onBack        = { navController.popBackStack() },
                    vm            = settingsVm,
                    onThemeChange = onThemeChange
                )
            }
            composable(NavRoutes.PLANETS) {
                PlanetsScreen(
                    onBack        = { navController.popBackStack() },
                    onPlanetClick = { name ->
                        navController.navigate(NavRoutes.planetDetail(name))
                    }
                )
            }
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

// ── Bottom Navigation Bar ──────────────────────────────────────────────────────
@Composable
private fun CosmosBottomNav(
    tabs: List<BottomTab>,
    navBackStack: androidx.navigation.NavBackStackEntry?,
    onTabSelected: (String) -> Unit
) {
    Surface(
        color           = MaterialTheme.colorScheme.surface,
        tonalElevation  = Dimens.elevationSm,
        shadowElevation = Dimens.elevationSm
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.bottomNavHeight)
                .navigationBarsPadding()
                .padding(horizontal = Dimens.spaceSm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            tabs.forEach { tab ->
                val selected = navBackStack?.destination
                    ?.hierarchy?.any { it.route == tab.route } == true
                CosmosNavItem(
                    tab      = tab,
                    selected = selected,
                    onClick  = { onTabSelected(tab.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CosmosNavItem(
    tab: BottomTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor   = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
    val iconColor     = if (selected) activeColor else inactiveColor
    val icon          = if (selected) tab.iconFilled else tab.iconOutlined
    val indicatorBg   = MaterialTheme.colorScheme.primaryContainer
    val label         = androidx.compose.ui.res.stringResource(tab.labelRes)

    Column(
        modifier = modifier
            .height(Dimens.bottomNavHeight)
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Pill indicator
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(Dimens.radiusFull))
                .background(if (selected) indicatorBg else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = iconColor,
                modifier           = Modifier.size(Dimens.iconMd)
            )
        }
        Spacer(Modifier.height(Dimens.spaceXxs))
        Text(
            text       = label,
            fontSize   = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color      = iconColor,
            maxLines   = 1
        )
    }
}
