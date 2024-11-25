package com.laru.auth.presentation

import com.laru.ui.model.ScreenState


data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val screenState: ScreenState = ScreenState.Idle,
)
