package com.example.spybot.network

import com.example.spybot.utils.TelegramResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TelegramApiService {
    @GET("sendMessage")
    fun sendMessage(
        @Query("chat_id") chatId: String,
        @Query("text") text: String,
        @Query("parse_mode") parseMode: String = "HTML"
    ): Call<TelegramResponse>
}
