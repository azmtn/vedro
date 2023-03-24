package com.example.homework_2.model

data class ReactionCounterItem(
    val code: String,
    val count: Int,
    var selectedByCurrentUser: Boolean = false
)
