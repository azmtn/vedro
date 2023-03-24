package com.example.homework_2.model

data class TopicItem (
    val topicName: String,
    val streamName: String,
    val color: Int,
    val messages: MutableList<Any>
)
