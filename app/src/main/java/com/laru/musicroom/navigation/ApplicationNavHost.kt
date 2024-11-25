package com.laru.musicroom.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.laru.profile.navigation.ProfileRoute
import com.laru.profile.navigation.profileScreen


@Composable
fun ApplicationNavHost(
    navHostController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navHostController,
        startDestination = ProfileRoute,
    ) {
        profileScreen() // TODO profile screen navigation
    }
}
