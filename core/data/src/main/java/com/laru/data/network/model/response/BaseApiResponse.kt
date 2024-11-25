package com.laru.data.network.model.response


data class BaseApiResponse<T>(
    val code: Int,
    val message: String?,
    val data: T?
)
