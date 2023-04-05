package com.example.homework_2.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicsListResponse (

    @SerialName("topics")
    val topics: List<TopicItem>
)
