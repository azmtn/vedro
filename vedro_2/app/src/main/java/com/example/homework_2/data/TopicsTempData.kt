package com.example.homework_2.data

import com.example.homework_2.R
import com.example.homework_2.model.TopicItem

internal var topicsItems = listOf(
    TopicItem(
        topicName = "Testing",
        streamName = "general",
        color = R.color.teal_700,
        messages = messagesItem
    ),
    TopicItem(
        topicName = "Bruh",
        streamName = "general",
        color = R.color.sepia,
        messages = mutableListOf()
    ),
    TopicItem(
        topicName = "Testing",
        streamName = "general",
        color = R.color.teal_700,
        messages = mutableListOf()
    )
)
