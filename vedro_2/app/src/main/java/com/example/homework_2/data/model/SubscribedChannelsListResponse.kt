package com.example.homework_2.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SubscribedChannelsListResponse (

    @SerialName("subscriptions")
    val subscriptions: List<StreamItem>
)
