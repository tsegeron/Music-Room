package com.laru.data.network.model.request

import com.squareup.moshi.Json


data class SignInRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "deviceId") val deviceId: String
)
