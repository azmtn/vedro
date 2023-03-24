package com.example.homework_2.presentation.adapter

import android.view.View
import com.example.homework_2.model.TopicItem

internal interface TopicItemClickListener {

    fun topicItemClickListener(topicItemView: View?, topic: TopicItem)

}
