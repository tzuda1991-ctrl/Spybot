package com.example.spybot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val etToken = findViewById<EditText>(R.id.et_bot_token)
        val etChatId = findViewById<EditText>(R.id.et_chat_id)
        val btnSave = findViewById<Button>(R.id.btn_save)

        // Load existing values if any
        etToken.setText(AppPreferences.getToken(this) ?: "")
        etChatId.setText(AppPreferences.getChatId(this) ?: "")

        btnSave.setOnClickListener {
            val token = etToken.text.toString().trim()
            val chatId = etChatId.text.toString().trim()

            if (token.isNotEmpty() && chatId.isNotEmpty()) {
                AppPreferences.saveToken(this, token)
                AppPreferences.saveChatId(this, chatId)
                Toast.makeText(this, "Settings Saved! Restart Service.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please fill both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
