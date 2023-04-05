package com.example.homework_2.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPresence (

    @SerialName("status")
    val status: String,

    @SerialName("timestamp")
    val timestamp: Long
)
