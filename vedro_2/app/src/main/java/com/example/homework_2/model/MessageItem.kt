package com.example.homework_2.model

import java.time.LocalDateTime

data class MessageItem(
    val text: String,
    val userId: Int,
    val topicName: String,
    val reactions: List<ReactionByUserItem>,
    val sendDateTime: LocalDateTime,
    var id: Int
)
