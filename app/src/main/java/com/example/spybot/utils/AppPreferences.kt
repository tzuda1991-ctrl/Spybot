package com.example.spybot.utils

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    
    private const val PREFS_NAME = "spy_bot_prefs"
    private const val KEY_TOKEN = "bot_token"
    private const val KEY_CHAT_ID = "chat_id"

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun saveChatId(context: Context, chatId: String) {
        getPrefs(context).edit().putString(KEY_CHAT_ID, chatId).apply()
    }

    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_TOKEN, null)
    }

    fun getChatId(context: Context): String? {
        return getPrefs(context).getString(KEY_CHAT_ID, null)
    }
}
