package com.laru.data.repo

import android.os.Build
import com.laru.data.model.AuthState
import com.laru.data.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


interface AuthRepository {

    val authState: StateFlow<AuthState>

    suspend fun signUp(
        username: String,
        email: String,
        password: String,
        deviceId: String = "${Build.MANUFACTURER} ${Build.MODEL} ${Build.ID}"
    ): Flow<Result<Unit>>

    suspend fun signIn(
        email: String,
        password: String,
        deviceId: String = "${Build.MANUFACTURER} ${Build.MODEL} ${Build.ID}"
    ): Flow<Result<Unit>>

    suspend fun authViaGoogle(
        email: String,
        accessToken: String,
        deviceId: String = "${Build.MANUFACTURER} ${Build.MODEL} ${Build.ID}"
    ): Flow<Result<Unit>>

    suspend fun passwordRestoreSendInstructions(email: String): Flow<Result<Unit>>
    suspend fun passwordRestoreSubmitNewPassword(token: String, newPassword: String): Flow<Result<Unit>>

    suspend fun logout(): Flow<Result<Unit>>

//    suspend fun refreshToken(): Flow<Result<Unit>>

    suspend fun isLoggedIn()
}
