package com.laru.data.repo

import android.util.Log
import com.laru.data.di.PrimaryAuthService
import com.laru.data.model.AuthState
import com.laru.data.model.Result
import com.laru.data.network.AuthApiService
import com.laru.data.network.TokenProvider
import com.laru.data.network.model.request.SignInRequest
import com.laru.data.network.model.request.LogoutRequest
import com.laru.data.network.model.request.SignInGoogleRequest
import com.laru.data.network.model.request.SignUpRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    @PrimaryAuthService private val authApi: AuthApiService,
    private val tokenProvider: TokenProvider,
) : AuthRepository {

    private val _authState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Unauthorized)
    override val authState = _authState.asStateFlow()

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            isLoggedIn()
        }
    }

    override suspend fun signUp(
        username: String,
        email: String,
        password: String,
        deviceId: String,
    ): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val response = authApi.signUp(SignUpRequest(username, email, password, deviceId))
            Log.i(TAG, "signUp code ${response.code()}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.data != null) {
                    tokenProvider.saveAccessToken(apiResponse.data.accessToken)
                    tokenProvider.saveRefreshToken(apiResponse.data.refreshToken)
                    _authState.update { AuthState.Authorized }
                    emit(Result.Success(Unit))
                } else {
                    _authState.update { AuthState.Unauthorized }
                    throw Exception("No tokens were provided")
                }
            } else {
                val errMsg = response.body()?.message ?: "Unknown error"
                throw Exception(errMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            _authState.update { AuthState.Unauthorized }
            emit(Result.Error(e))
        }
    }

    override suspend fun signIn(
        email: String,
        password: String,
        deviceId: String,
    ): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val response = authApi.signIn(SignInRequest(email, password, deviceId))
            Log.i(TAG, "signIn code ${response.code()}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.data != null) {
                    tokenProvider.saveAccessToken(apiResponse.data.accessToken)
                    tokenProvider.saveRefreshToken(apiResponse.data.refreshToken)
                    _authState.update { AuthState.Authorized }
                    emit(Result.Success(Unit))
                } else {
                    _authState.update { AuthState.Unauthorized }
                    throw Exception("No tokens were provided")
                }
            } else {
                val errMsg = response.body()?.message ?: "Unknown error"
                throw Exception(errMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            _authState.update { AuthState.Unauthorized }
            emit(Result.Error(e))
        }
    }

    override suspend fun authViaGoogle(
        email: String,
        accessToken: String,
        deviceId: String,
    ): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val response = authApi.authGoogle("Bearer $accessToken", SignInGoogleRequest(deviceId))
            Log.i(TAG, "signInGoogle code ${response.code()}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.data != null) {
                    tokenProvider.saveAccessToken(apiResponse.data.accessToken)
                    tokenProvider.saveRefreshToken(apiResponse.data.refreshToken)
                    _authState.update { AuthState.Authorized }
                    emit(Result.Success(Unit))
                } else {
                    _authState.update { AuthState.Unauthorized }
                    throw Exception("No tokens were provided")
                }
            } else {
                val errMsg = response.body()?.message ?: "Unknown error"
                throw Exception(errMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            _authState.update { AuthState.Unauthorized }
            emit(Result.Error(e))
        }
    }

    data class PasswordRestoreEmailRequest(val email: String)

    override suspend fun passwordRestoreSendInstructions(email: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val response = authApi.resetPassword(null, PasswordRestoreEmailRequest(email))
            Log.i(TAG, "passwordRestoreSendInstructions code ${response.code()}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                _authState.update { AuthState.Unauthorized }
                Log.i(TAG, "${response.code()}: ${apiResponse?.message}")
                emit(Result.Success(Unit))
            } else {
                val errMsg = response.body()?.message ?: "Unknown error"
                throw Exception(errMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            emit(Result.Error(e))
        }
    }

    data class PasswordRestoreSubmitRequest(val newPassword: String)

    override suspend fun passwordRestoreSubmitNewPassword(token: String, newPassword: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val response = authApi.resetPassword(token, PasswordRestoreSubmitRequest(newPassword))
            Log.i(TAG, "passwordRestoreSubmitNewPassword code ${response.code()}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                _authState.update { AuthState.Unauthorized }
                Log.i(TAG, "${response.code()}: ${apiResponse?.message}")
                emit(Result.Success(Unit))
            } else {
                val errMsg = response.body()?.message ?: "Unknown error"
                throw Exception(errMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            emit(Result.Error(e))
        }
    }

    override suspend fun logout(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val refreshToken = tokenProvider.getRefreshToken()!!
            val response = authApi.logout(LogoutRequest(refreshToken))
            Log.i(TAG, "logout code ${response.code()}")

            if (response.isSuccessful) {
                tokenProvider.removeAccessToken()
                tokenProvider.removeRefreshToken()
                _authState.update { AuthState.Unauthorized }
                emit(Result.Success(Unit))
            } else {
                val errMsg = response.body()?.message ?: "Unknown error"
                throw Exception(errMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            _authState.update { AuthState.Unauthorized }
            emit(Result.Error(e))
        }
    }

    /*
    override suspend fun refreshToken(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val accessToken = tokenProvider.getAccessToken()
            val refreshToken = tokenProvider.getRefreshToken()
            if (refreshToken != null && accessToken != null) {
                val response = authApi.refreshToken(TokenRequest(accessToken, refreshToken))
                Log.i(TAG, "refreshToken code ${response.code()}")

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.data != null) {
                        tokenProvider.saveAccessToken(apiResponse.data.accessToken)
                        tokenProvider.saveRefreshToken(apiResponse.data.refreshToken)
                        _authState.update { AuthState.Authorized }
                        emit(Result.Success(Unit))
                    } else {
                        _authState.update { AuthState.Unauthorized }
                        Log.e(TAG, "${response.code()}: no data")
                        throw Exception("No tokens were provided")
                    }
                } else {
                    val errMsg = response.body()?.message ?: "Unknown error"
                    Log.e(TAG, "${response.code()}: $errMsg")
                    throw Exception(errMsg)
                }
            } else throw Exception("accessToken/refreshToken not found")
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            _authState.update { AuthState.Unauthorized }
            emit(Result.Error(e))
        }
    }
    */

    override suspend fun isLoggedIn() {
        try {
            val response = authApi.isLoggedIn()
            Log.i(TAG, "isLoggedIn code ${response.code()}")

            if (response.isSuccessful) {
                _authState.update { AuthState.Authorized }
            } else {
                Log.i(TAG, "${response.code()}: Unauthorized")
                throw Exception("Unauthorized")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            _authState.update { AuthState.Unauthorized }
        }
    }
    
    companion object {
        const val TAG = "AuthRepositoryImpl"
    }
}
