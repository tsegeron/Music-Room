package com.laru.musicroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laru.data.model.AuthState
import com.laru.data.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    authRepository: AuthRepository
): ViewModel() {
    val uiState = authRepository.authState.map { authState ->
        when (authState) {
            AuthState.Authorized -> MainActivityUiState.Authorized
            AuthState.Unauthorized -> MainActivityUiState.Unauthorized
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MainActivityUiState.Unauthorized)

}

sealed interface MainActivityUiState {
    data object Authorized: MainActivityUiState
    data object Unauthorized : MainActivityUiState
}
