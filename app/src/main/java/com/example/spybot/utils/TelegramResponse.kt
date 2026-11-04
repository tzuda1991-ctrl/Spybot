package com.example.spybot.utils

import com.google.gson.annotations.SerializedName

data class TelegramResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("description") val description: String? = null
)
