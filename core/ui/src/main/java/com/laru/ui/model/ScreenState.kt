package com.laru.ui.model

import com.laru.data.model.Result


sealed class ScreenState {
    data object Idle : ScreenState()
    data object Loading : ScreenState()
    data class Success(val message: String? = null) : ScreenState()
    data class Error(val error: String? = null) : ScreenState()
}

fun Result<Unit>.toRequestState() = when (this) {
    Result.Loading -> ScreenState.Loading
    is Result.Success -> ScreenState.Success()
    else -> ScreenState.Error()
}
