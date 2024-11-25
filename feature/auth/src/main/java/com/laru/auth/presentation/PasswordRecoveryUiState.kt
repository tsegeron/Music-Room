package com.laru.auth.presentation

import com.laru.ui.model.ScreenState


data class PasswordRecoveryUiState(
    val email: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val screenState: ScreenState = ScreenState.Idle,
)
