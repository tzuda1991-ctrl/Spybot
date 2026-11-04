package com.example.spybot.network

import com.example.spybot.utils.TelegramResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface TelegramApiService {
    @GET
    fun sendMessage(
        @Url url: String,
        @Query("chat_id") chatId: String,
        @Query("text") text: String,
        @Query("parse_mode") parseMode: String = "HTML"
    ): Call<TelegramResponse>
}
