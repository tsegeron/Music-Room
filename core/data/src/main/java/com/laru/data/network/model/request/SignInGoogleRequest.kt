package com.laru.data.network.model.request

import com.squareup.moshi.Json


data class SignInGoogleRequest(
    @Json(name = "deviceId") val deviceId: String
)
