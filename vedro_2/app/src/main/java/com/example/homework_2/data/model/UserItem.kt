package com.example.homework_2.data.model

import com.example.homework_2.data.networking.OauthPrefence.AUTH_ID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal const val SELF_USER_ID = AUTH_ID

@Serializable
data class UserItem(
    @SerialName("user_id")
    val userId: Long,

    @SerialName("full_name")
    val fullName: String? = "",

    @SerialName("email")
    val email: String? = "",

    @SerialName("avatar_url")
    val avatarUrl: String?,

    var presence: String? = "undefined"
)
