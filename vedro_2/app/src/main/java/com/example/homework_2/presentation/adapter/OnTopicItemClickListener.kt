package com.example.homework_2.presentation.adapter

import com.example.homework_2.data.model.TopicItem

internal interface OnTopicItemClickListener {

    fun topicItemClickListener(topicItem: TopicItem, channelName: String)

}
