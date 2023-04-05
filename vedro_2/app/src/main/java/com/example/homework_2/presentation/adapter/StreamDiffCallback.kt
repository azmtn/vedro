package com.example.homework_2.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.homework_2.data.model.StreamItem
import com.example.homework_2.data.model.TopicItem

class StreamDiffCallback : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is StreamItem && newItem is StreamItem) {
            return oldItem.name == newItem.name
        }
        if (oldItem is TopicItem && newItem is TopicItem) {
            return oldItem.name == newItem.name
        }
        return false
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is StreamItem && newItem is StreamItem) {
            return oldItem == newItem
        }
        if (oldItem is TopicItem && newItem is TopicItem) {
            return oldItem == newItem
        }
        return false
    }
}
