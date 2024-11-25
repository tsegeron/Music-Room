package com.laru.data.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PrimaryClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PrimaryAuthService

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshAuthService
