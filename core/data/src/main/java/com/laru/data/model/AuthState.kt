package com.laru.data.model


sealed interface AuthState {
    data object Authorized: AuthState
    data object Unauthorized: AuthState
}
