package com.example.homework_2.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.homework_2.data.model.MessageItem
import java.time.LocalDate

class MessageListDiffCallback : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is MessageItem && newItem is MessageItem) {
            return oldItem.id == newItem.id
        }
        if (oldItem is LocalDate && newItem is LocalDate) {
            return oldItem == newItem
        }
        return false
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is MessageItem && newItem is MessageItem) {
            return oldItem == newItem
        }
        if (oldItem is LocalDate && newItem is LocalDate) {
            return oldItem == newItem
        }
        return false
    }
}
