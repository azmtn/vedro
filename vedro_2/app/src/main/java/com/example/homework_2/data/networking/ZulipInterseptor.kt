package com.example.homework_2.data.networking

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request

class ZulipInterseptor(
    private val email: String,
    private val apiKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val credential: String = Credentials.basic(email, apiKey)
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credential)
            .build()
        return chain.proceed(authenticatedRequest)
    }
}