package com.example.homework_2.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.homework_2.model.StreamItem

class TopicsListDiffCallback : DiffUtil.ItemCallback<StreamItem>() {

    override fun areItemsTheSame(oldItem: StreamItem, newItem: StreamItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: StreamItem, newItem: StreamItem): Boolean {
        return oldItem == newItem
    }
}
