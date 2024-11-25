package com.laru.data.network

interface TokenProvider {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveAccessToken(token: String)
    fun saveRefreshToken(token: String)
    fun removeAccessToken()
    fun removeRefreshToken()
}

