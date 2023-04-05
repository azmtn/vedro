package com.example.homework_2.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessagesListResponse (

    @SerialName("messages")
    val messages: List<MessageItem>
)
