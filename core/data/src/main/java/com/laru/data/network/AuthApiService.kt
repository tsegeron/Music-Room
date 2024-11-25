package com.laru.data.network

import com.laru.data.network.model.response.BaseApiResponse
import com.laru.data.network.model.request.SignInRequest
import com.laru.data.network.model.request.LogoutRequest
import com.laru.data.network.model.request.SignInGoogleRequest
import com.laru.data.network.model.request.SignUpRequest
import com.laru.data.network.model.request.TokenRequest
import com.laru.data.network.model.response.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface AuthApiService {

    @POST("/auth/signin/google")
    suspend fun authGoogle(
        @Header("Authorization") accessToken: String,
        @Body request: SignInGoogleRequest
    ): Response<BaseApiResponse<TokenResponse>>

    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<BaseApiResponse<TokenResponse>>

    @POST("/auth/signin")
    suspend fun signIn(@Body request: SignInRequest): Response<BaseApiResponse<TokenResponse>>

    @POST("/auth/reset-password")
    suspend fun resetPassword(
        @Query("reset_token") token: String?,
        @Body request: Any
    ): Response<BaseApiResponse<Any>>

    @POST("/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<BaseApiResponse<Any>>

    @POST("/auth/refresh")
    suspend fun refreshToken(@Body token: TokenRequest): Response<BaseApiResponse<TokenResponse>>

    @GET("/auth")
    suspend fun isLoggedIn(): Response<BaseApiResponse<Any>>
}
