package com.example.homework_2.data

import com.example.homework_2.model.*
import java.time.LocalDate
import java.time.LocalDateTime

internal var messagesItem = mutableListOf(
    LocalDate.now().minusDays(1),
    MessageItem(
        id = 1,
        userId = 2,
        text = "Привет. Как дела?",
        topicName = "Testing",
        reactions = listOf(
            ReactionByUserItem(userId = 1, code = "🧠"),
            ReactionByUserItem(userId = 2, code = "👋"),
            ReactionByUserItem(userId = 2, code = "🧠"),
            ReactionByUserItem(userId = 1, code = "👋"),
            ReactionByUserItem(userId = 2, code = "👋"),
            ReactionByUserItem(userId = 6, code = "👍"),
            ReactionByUserItem(userId = 7, code = "👍"),
            ReactionByUserItem(userId = 8, code = "👍"),
            ReactionByUserItem(userId = 9, code = "👍"),
        ),
        sendDateTime = LocalDateTime.now().minusDays(1)
    ),
    LocalDate.now(),
    MessageItem(
        id = 3,
        userId = 1,
        text = "Привет, я опять с \uD83D\uDD29\uD83D\uDD29",
        topicName = "Testing",
        reactions = listOf(),
        sendDateTime = LocalDateTime.now().minusDays(1)
    ),
    MessageItem(
        id = 4,
        userId = 2,
        text = "пока что были легкие домашки)) дальше интереснее будет)",
        topicName = "Testing",
        reactions = listOf(
            ReactionByUserItem(userId = 1, code = "👩"),
            ReactionByUserItem(userId = 2, code = "🧑‍"),
            ReactionByUserItem(userId = 2, code = "✊"),
            ReactionByUserItem(userId = 2, code = "🧑‍"),
            ReactionByUserItem(userId = 2, code = "✊"),
            ReactionByUserItem(userId = 1, code = "👋"),
        ),
        sendDateTime = LocalDateTime.now()
    )
)

internal fun getUserById(userId: Int): UserItem? {
    return usersitems.firstOrNull { it.id == userId }
}

internal var usersitems = mutableListOf(
    UserItem(
        id = 1,
        name = "Main User",
        email = "mymail@gmail.com",
        status = "on work",
        online = true
    ),
    UserItem(
        id = 2,
        name = "Kir Ill",
        email = "kirIII@gmail.com",
        status = "busy",
        online = true
    ),
    UserItem(
        id = 3,
        name = "No name",
        email = "noname@gmail.com",
        status = "on work",
        online = false
    )
)

internal val streamsItems = listOf(
    StreamItem(
        name = "general",
        topics = topicsItems
    ),
    StreamItem(
        name = "Development",
        topics = listOf()
    ),
    StreamItem(
        name = "Design",
        topics = listOf()
    ),
    StreamItem(
        name = "HR",
        topics = listOf()
    )
)

