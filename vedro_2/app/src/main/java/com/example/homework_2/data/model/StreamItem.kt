package com.example.homework_2.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamItem(

    @SerialName("stream_id")
    val id: Long,

    @SerialName("name")
    val name: String,

    var topics: List<TopicItem>,
)
