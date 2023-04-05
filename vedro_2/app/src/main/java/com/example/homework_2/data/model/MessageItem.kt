package com.example.homework_2.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageItem(
    @SerialName("id")
    val id: Long,

    @SerialName("sender_id")
    val userId: Long,

    @SerialName("sender_full_name")
    val userFullName: String,

    @SerialName("subject")
    val topicName: String,

    @SerialName("avatar_url")
    val avatarUrl: String?,

    @SerialName("content")
    val content: String,

    @SerialName("reactions")
    val reactions: List<Reaction>,

    @SerialName("timestamp")
    val timestamp: Long
)