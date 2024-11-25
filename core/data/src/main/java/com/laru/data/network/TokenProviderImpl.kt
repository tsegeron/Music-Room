package com.laru.data.network

import android.content.SharedPreferences
import javax.inject.Inject


class TokenProviderImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
): TokenProvider {

    override fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)

    override fun getRefreshToken(): String? = sharedPreferences.getString("refresh_token", null)

    override fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString("access_token", token).apply()
    }

    override fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString("refresh_token", token).apply()
    }

    override fun removeAccessToken() {
        sharedPreferences.edit().remove("access_token").apply()
    }

    override fun removeRefreshToken() {
        sharedPreferences.edit().remove("refresh_token").apply()
    }

}
