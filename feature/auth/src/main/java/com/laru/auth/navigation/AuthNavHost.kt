package com.laru.auth.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.laru.auth.presentation.AuthViewModel


@Composable
fun AuthNavHost(
    navHostController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navHostController,
        startDestination = SignInRoute,
    ) {
        signInScreen(
            navigateToSignUp = navHostController::navigateToSignUpScreen,
            navigateToPasswordRestore = navHostController::navigateToPasswordRecoveryEmailScreen,
            viewModel = authViewModel
        )

        signUpScreen(
            navigateBack = navHostController::popBackStack,
            viewModel = authViewModel
        )

        passwordRecoveryEmail(
            navigateBack = navHostController::popBackStack,
            viewModel = authViewModel
        )

        passwordRecovery(
            navigateOnSuccess = navHostController::popBackStack,
            viewModel = authViewModel
        )
    }
}
