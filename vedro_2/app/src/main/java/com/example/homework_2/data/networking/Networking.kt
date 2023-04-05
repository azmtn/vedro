package com.example.homework_2.data.networking

import com.example.homework_2.data.networking.OauthPrefence.AUTH_EMAIL
import com.example.homework_2.data.networking.OauthPrefence.AUTH_KEY
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

object Networking {
    private const val BASE_URL = "https://tinkoff-android-spring-2023.zulipchat.com/"

    private val contentType = "application/json".toMediaType()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ZulipInterseptor(AUTH_EMAIL, AUTH_KEY))
        .build()

    private var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory(contentType))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    fun getZulipApi(): ZulipApi {
        return retrofit.create(ZulipApi::class.java)
    }

}