package com.laru.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.laru.auth.screen.PasswordRecoveryEmailScreen
import com.laru.auth.screen.PasswordRecoveryScreen
import com.laru.auth.screen.SignInScreen
import com.laru.auth.screen.SignUpScreen
import com.laru.auth.presentation.AuthViewModel
import kotlinx.serialization.Serializable


@Serializable data object SignInRoute
@Serializable data object SignUpRoute
@Serializable data object PasswordRecoveryEmailRoute
@Serializable data class PasswordRecoveryPasswordRoute(val resetToken: String)


fun NavController.navigateToSignInScreen() = navigate(SignInRoute) {
    popUpTo(graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

fun NavGraphBuilder.signInScreen(
    navigateToSignUp: () -> Unit,
    navigateToPasswordRestore: () -> Unit,
    viewModel: AuthViewModel
) {
    composable<SignInRoute> {
        SignInScreen(
            navigateToSignUp = navigateToSignUp,
            navigateToPasswordRestore = navigateToPasswordRestore,
            viewModel = viewModel
        )
    }
}


fun NavController.navigateToSignUpScreen() = navigate(SignUpRoute) {
    popUpTo(graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

fun NavGraphBuilder.signUpScreen(
    navigateBack: () -> Unit,
    viewModel: AuthViewModel
) {
    composable<SignUpRoute> {
        SignUpScreen(
            navigateBack = navigateBack,
            viewModel = viewModel
        )
    }
}


fun NavController.navigateToPasswordRecoveryEmailScreen() = navigate(PasswordRecoveryEmailRoute) {
    popUpTo(graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

fun NavGraphBuilder.passwordRecoveryEmail(
    navigateBack: () -> Unit,
    viewModel: AuthViewModel
) {
    composable<PasswordRecoveryEmailRoute> {
        PasswordRecoveryEmailScreen(
            navigateBack = navigateBack,
            viewModel = viewModel
        )
    }
}

fun NavGraphBuilder.passwordRecovery(
    navigateOnSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    composable<PasswordRecoveryPasswordRoute>(
        deepLinks = listOf(
            navDeepLink<PasswordRecoveryPasswordRoute>(
                basePath = "mroom://music-room/reset-password"
            )
        )
    ) { backStackEntry ->
        val resetToken = backStackEntry.toRoute<PasswordRecoveryPasswordRoute>().resetToken
        viewModel.savePasswordRecoveryToken(resetToken)

        PasswordRecoveryScreen(
            navigateOnSuccess = navigateOnSuccess,
            viewModel = viewModel
        )
    }
}
