package com.laru.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.laru.profile.ProfileScreen
import kotlinx.serialization.Serializable


@Serializable
data object ProfileRoute

fun NavController.navigateToProfile() = navigate(ProfileRoute) {
    popUpTo(graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

fun NavGraphBuilder.profileScreen() = composable<ProfileRoute> {
    ProfileScreen()
}
