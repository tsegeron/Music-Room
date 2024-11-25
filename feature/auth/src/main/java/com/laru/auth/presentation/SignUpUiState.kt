package com.laru.auth.presentation

import com.laru.ui.model.ScreenState


data class SignUpUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isTermsAccepted: Boolean = false,
    val screenState: ScreenState = ScreenState.Idle,
)
