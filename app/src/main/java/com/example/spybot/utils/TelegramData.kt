package com.example.spybot.utils

data class TelegramResponse(
    val ok: Boolean,
    val result: List<Result>? = null
)

data class Result(
    val message_id: Int
)
