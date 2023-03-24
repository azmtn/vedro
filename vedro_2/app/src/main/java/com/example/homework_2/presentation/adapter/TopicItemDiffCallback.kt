package com.example.homework_2.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.homework_2.model.TopicItem

class TopicItemDiffCallback : DiffUtil.ItemCallback<TopicItem>() {

    override fun areItemsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
        return oldItem.topicName == newItem.topicName
    }

    override fun areContentsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
        return oldItem == newItem
    }
}
