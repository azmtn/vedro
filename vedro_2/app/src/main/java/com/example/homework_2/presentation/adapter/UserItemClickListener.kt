package com.example.homework_2.presentation.adapter

import android.view.View
import com.example.homework_2.model.UserItem

internal interface UserItemClickListener {

    fun userItemClickListener(topicItemView: View?, user: UserItem)

}