package com.laru.data.network

import android.util.Log
import com.laru.data.network.model.request.TokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import javax.inject.Inject


class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val authApiService: AuthApiService
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val accessToken = tokenProvider.getAccessToken()
        if (!accessToken.isNullOrBlank()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }

        val response = chain.proceed(request)
        if (response.code() == 401) {
            Log.i("AuthInterceptor", "refreshing token")
            synchronized(this) {
                val newAccessToken = runBlocking { refreshToken() } // since interceptors require synchronous behaviour

                if (newAccessToken != null) {
                    request = request.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()
                    response.close()
                    return chain.proceed(request)
                }
            }
        }
        return response
    }

    private suspend fun refreshToken(): String? {
        return try {
            val accessToken = tokenProvider.getAccessToken()
            val refreshToken = tokenProvider.getRefreshToken()
            if (refreshToken != null && accessToken != null) {
                val response = authApiService.refreshToken(TokenRequest(accessToken, refreshToken))

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.data != null) {
                        tokenProvider.saveAccessToken(apiResponse.data.accessToken)
                        tokenProvider.saveRefreshToken(apiResponse.data.refreshToken)

                        apiResponse.data.accessToken
                    } else throw Exception("Server response data is null")
                } else {
                    val errMsg = response.errorBody()?.string()?.let {
                        JSONObject(it).getString("errorMessage")
                    } ?: run {
                        response.code().toString()
                    }
                    throw Exception(errMsg)
                }
            } else throw Exception("accessToken/refreshToken not found")
        } catch (e: Exception) {
            Log.e("AuthInterceptor", e.message ?: "Unknown error in TokenRepository")
            null
        }
    }
}
