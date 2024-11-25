package com.laru.data.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.laru.data.BuildConfig
import com.laru.data.network.AuthApiService
import com.laru.data.network.AuthInterceptor
import com.laru.data.network.TokenProvider
import com.laru.data.network.TokenProviderImpl
import com.laru.data.repo.AuthRepository
import com.laru.data.repo.AuthRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideGoogleIdOption(): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // Query all google accounts on the device
            .setServerClientId(BuildConfig.SERVER_CLIENT_ID)
            .build()

//        TODO refactor
//        return GetGoogleIdOption.Builder()
//            .setFilterByAuthorizedAccounts(true)
//            .setServerClientId(BuildConfig.SERVER_CLIENT_ID)
//            .setAutoSelectEnabled(true) // Enables automatic sign-in for returning users
//            .setNonce(<nonce string to use when generating a Google ID token>) // Configure nonce https://developer.android.com/identity/sign-in/credential-manager-siwg#set-nonce
//            .build()
    }

    @Provides
    @Singleton
    fun provideCredentialRequest(googleIdOption: GetGoogleIdOption): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences("tokens", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTokenProvider(sharedPreferences: SharedPreferences): TokenProvider {
        return TokenProviderImpl(sharedPreferences)
    }

    @RefreshClient
    @Provides
    @Singleton
    fun provideRefreshOkHttpClient(): OkHttpClient {
        // Basic OkHttpClient without AuthInterceptor for token refresh requests
        return OkHttpClient.Builder().build()
    }

    @RefreshAuthService
    @Provides
    @Singleton
    fun provideRefreshAuthApiService(
        @RefreshClient refreshClient: OkHttpClient,
        moshi: Moshi
    ): AuthApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_BASE_URL)
            .client(refreshClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenProvider: TokenProvider,
        @RefreshAuthService refreshAuthApiService: AuthApiService
    ): AuthInterceptor = AuthInterceptor(tokenProvider, refreshAuthApiService)

    @PrimaryClient
    @Provides
    @Singleton
    fun providePrimaryOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshiConverter(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @PrimaryAuthService
    @Provides
    @Singleton
    fun provideAuthApiService(
        @PrimaryClient primaryClient: OkHttpClient,
        moshi: Moshi
    ): AuthApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_BASE_URL)
            .client(primaryClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @PrimaryAuthService authApiService: AuthApiService,
        tokenProvider: TokenProvider,
    ): AuthRepository {
        return AuthRepositoryImpl(authApiService, tokenProvider)
    }
}
