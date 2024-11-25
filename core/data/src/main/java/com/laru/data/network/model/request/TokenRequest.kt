package com.laru.data.network.model.request


data class TokenRequest(
    val accessToken: String,
    val refreshToken: String,
)
