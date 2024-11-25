package com.laru.auth.presentation

import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.laru.data.repo.AuthRepository
import com.laru.ui.model.toRequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val credentialRequest: GetCredentialRequest
): ViewModel() {

    private val _signInUiState = MutableStateFlow(SignInUiState())
    val signInUiState = _signInUiState.asStateFlow()

    private val _signUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState = _signUpUiState.asStateFlow()

    private val _passwordRecoveryUiState = MutableStateFlow(PasswordRecoveryUiState())
    val passwordRecoveryUiState = _passwordRecoveryUiState.asStateFlow()

    private val _passwordRecoveryToken = MutableStateFlow("")


    fun savePasswordRecoveryToken(token: String) {
        _passwordRecoveryToken.update { token }
    }

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnEmailChange -> updateEmail(action.value)
            is AuthAction.OnSignInPasswordChange -> updateSignInPassword(action.value)
            is AuthAction.OnSignUpUsernameChange -> updateSignUpUsername(action.value)
            is AuthAction.OnSignUpPasswordChange -> updateSignUpPassword(action.value)
            is AuthAction.OnSignUpConfirmPasswordChange -> updateSignUpConfirmPassword(action.value)
            is AuthAction.OnRecoverPasswordChange -> updatePasswordRecoveryNewPassword(action.value)
            is AuthAction.OnRecoverConfirmPasswordChange -> updatePasswordRecoveryConfirmNewPassword(action.value)

            AuthAction.OnSignInClick -> onSignIn()
            AuthAction.OnSignUpClick -> onSignUp()
            AuthAction.OnAgreeWithTermsClick -> onTermsAccept()
            AuthAction.OnSendInstructionsClick -> onPasswordRestoreSendInstructions()
            AuthAction.OnRecoverPasswordClick -> onPasswordRestoreSubmitNewPassword()
        }
    }


    private fun updateEmail(updatedEmail: String) {
        _signInUiState.update { it.copy(email = updatedEmail) }
        _signUpUiState.update { it.copy(email = updatedEmail) }
        _passwordRecoveryUiState.update { it.copy(email = updatedEmail) }
    }

    private fun updateSignInPassword(updatedPassword: String) {
        _signInUiState.update { it.copy(password = updatedPassword) }
    }

    private fun updateSignUpUsername(updatedUsername: String) {
        _signUpUiState.update { it.copy(username = updatedUsername) }
    }

    private fun updateSignUpPassword(updatedPassword: String) {
        _signUpUiState.update { it.copy(password = updatedPassword) }
    }

    private fun updateSignUpConfirmPassword(confirmPassword: String) {
        _signUpUiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    private fun onTermsAccept() {
        _signUpUiState.update { it.copy(isTermsAccepted = !it.isTermsAccepted) }
    }

    private fun updatePasswordRecoveryNewPassword(newPassword: String) {
        _passwordRecoveryUiState.update { it.copy(newPassword = newPassword) }
    }

    private fun updatePasswordRecoveryConfirmNewPassword(confirmNewPassword: String) {
        _passwordRecoveryUiState.update { it.copy(confirmNewPassword = confirmNewPassword) }
    }

    private fun onSignIn() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signIn(_signInUiState.value.email, _signInUiState.value.password)
                .collect { result ->
                    _signInUiState.update { it.copy(screenState = result.toRequestState()) }
                }
        }
    }

    private fun onSignUp() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signUp(
                username = _signUpUiState.value.username,
                email = _signUpUiState.value.email,
                password = _signUpUiState.value.password
            ).collect { result ->
                _signUpUiState.update { it.copy(screenState = result.toRequestState()) }
            }
        }
    }

    private fun onPasswordRestoreSendInstructions() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.passwordRestoreSendInstructions(_passwordRecoveryUiState.value.email)
                .collect { result ->
                    _passwordRecoveryUiState.update { it.copy(screenState = result.toRequestState()) }
                }
        }
    }

    private fun onPasswordRestoreSubmitNewPassword() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository
                .passwordRestoreSubmitNewPassword(
                    _passwordRecoveryToken.value,
                    _passwordRecoveryUiState.value.newPassword
                ).collect { result ->
                    _passwordRecoveryUiState.update { it.copy(screenState = result.toRequestState()) }
                }
        }
    }

    /**
     * Handles the successfully returned google credential
     * Tries to [AuthRepository.authViaGoogle] using google credential
     *
     * @param result the result of [androidx.credentials.CredentialManager.getCredential]
     */
    suspend fun handleGoogleAuth(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        authRepository.authViaGoogle(
                            email = googleIdTokenCredential.id,
                            accessToken = googleIdTokenCredential.idToken
                        ).collect { requestResult ->
                            _signUpUiState.update { it.copy(screenState = requestResult.toRequestState()) }
                            _signInUiState.update { it.copy(screenState = requestResult.toRequestState()) }
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("handleGoogleAuth", "handleSignIn:", e)
                    }
                } else {
                    Log.e("handleGoogleAuth", "Unexpected type of credential 1")
                }
            }

            else -> {
                Log.e("handleGoogleAuth", "Unexpected type of credential 2")
            }
        }
    }
}
