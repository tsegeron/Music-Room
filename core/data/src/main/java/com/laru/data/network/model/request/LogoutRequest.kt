package com.laru.data.network.model.request

import com.squareup.moshi.Json


data class LogoutRequest(
    @Json(name = "refreshToken") val refreshToken: String
)
