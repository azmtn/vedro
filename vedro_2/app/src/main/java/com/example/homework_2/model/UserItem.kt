package com.example.homework_2.model

data class UserItem (
    val id: Int,
    val name: String,
    val email: String,
    val status: String,
    val online: Boolean = false
)
