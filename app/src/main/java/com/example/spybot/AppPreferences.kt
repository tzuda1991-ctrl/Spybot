package com.example.spybot

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val PREF_NAME = "spybot_prefs"
    private const val KEY_BOT_TOKEN = "bot_token"
    private const val KEY_CHAT_ID = "chat_id"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        getPreferences(context).edit().putString(KEY_BOT_TOKEN, token.trim()).apply()
    }

    fun getToken(context: Context): String? {
        return getPreferences(context).getString(KEY_BOT_TOKEN, null)?.trim()
    }

    fun saveChatId(context: Context, chatId: String) {
        getPreferences(context).edit().putString(KEY_CHAT_ID, chatId.trim()).apply()
    }

    fun getChatId(context: Context): String? {
        return getPreferences(context).getString(KEY_CHAT_ID, null)?.trim()
    }
}
