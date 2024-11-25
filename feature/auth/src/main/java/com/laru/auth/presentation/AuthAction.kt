package com.laru.auth.presentation

sealed interface AuthAction {
    data class OnEmailChange(val value: String): AuthAction

    data class OnSignInPasswordChange(val value: String): AuthAction
    data object OnSignInClick: AuthAction

    data class OnSignUpUsernameChange(val value: String): AuthAction
    data class OnSignUpPasswordChange(val value: String): AuthAction
    data class OnSignUpConfirmPasswordChange(val value: String): AuthAction
    data object OnAgreeWithTermsClick: AuthAction
    data object OnSignUpClick: AuthAction

    data class OnRecoverPasswordChange(val value: String): AuthAction
    data class OnRecoverConfirmPasswordChange(val value: String): AuthAction
    data object OnSendInstructionsClick: AuthAction
    data object OnRecoverPasswordClick: AuthAction
}
