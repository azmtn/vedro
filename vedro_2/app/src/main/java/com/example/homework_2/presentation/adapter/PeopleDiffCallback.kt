package com.example.homework_2.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.homework_2.model.UserItem

class PeopleDiffCallback : DiffUtil.ItemCallback<UserItem>() {

    override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
        return oldItem == newItem
    }
}
