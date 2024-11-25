package com.laru.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laru.data.model.Result
import com.laru.data.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


//data class ProfileUiState(
//    val status: String
//)

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository // TODO remove
): ViewModel() {
    private val _uiState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val uiState = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.logout().collect { result ->
                when (result) {
                    Result.Loading -> {}
                    is Result.Success -> {
                        _uiState.update { false }
                        Log.i("ProfileScreenViewModel", "logged out")
                    }
                    else -> _uiState.update { true }
                }
            }
        }
    }
}
