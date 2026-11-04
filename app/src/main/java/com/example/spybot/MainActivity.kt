package com.example.spybot

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btn_start)
        tvStatus = findViewById(R.id.tv_status)

        checkPermission()
        
        btnStart.setOnClickListener {
            startService(Intent(this, NotificationListener::class.java))
            Toast.makeText(this, "Spy Mode Started!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission() {
        // Check if we have permission to post notifications (Android 13+)
        val hasNotificationPermission = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        if (!hasNotificationPermission) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // Check Notification Listener Setting
        val listenerEnabled = Settings.Secure.getString(contentResolver, "enabled_notification_listeners").contains(packageName)
        
        if (!listenerEnabled) {
            tvStatus.text = "Please enable Notification Access in Settings."
            btnStart.isEnabled = false
            
            // Open Settings directly
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        } else {
            tvStatus.text = "System Ready. Press Start."
            btnStart.isEnabled = true
        }
    }
}
