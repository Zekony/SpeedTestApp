package com.zekony.speed.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun AppBottomBar(navController: NavController) {
    val currentDestination by navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = Modifier
            .height(60.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        NavigationButtons.values().forEach { navBtn ->
            val isSelected = currentDestination?.destination?.route == navBtn.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) navController.navigate(navBtn.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primary),
                icon = {
                    Icon(
                        if (isSelected) {
                            navBtn.selectedIcon
                        } else navBtn.unselectedIcon,
                        contentDescription = navBtn.name
                    )
                }
            )
        }
    }
}