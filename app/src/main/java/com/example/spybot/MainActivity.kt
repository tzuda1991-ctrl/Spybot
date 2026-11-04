package com.example.spybot

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
        btnSettings = findViewById(R.id.btn_settings)
        tvStatus = findViewById(R.id.tv_status)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        btnStart.setOnClickListener {
            if (!isNotificationListenerEnabled()) {
                Toast.makeText(this, "Notification Access allow karein", Toast.LENGTH_SHORT).show()
                openNotificationAccessSettings()
            } else {
                toggleNotificationListenerService()
                Toast.makeText(this, "Spy Mode Active!", Toast.LENGTH_SHORT).show()
                updateUiStatus()
            }
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateUiStatus()
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return enabledListeners != null && enabledListeners.contains(packageName)
    }

    private fun updateUiStatus() {
        if (!isNotificationListenerEnabled()) {
            tvStatus.text = "Please enable Notification Access in Settings."
            btnStart.text = "ENABLE PERMISSION"
        } else {
            tvStatus.text = "System Ready. Monitoring Active."
            btnStart.text = "RESTART LISTENER"
        }
    }

    private fun openNotificationAccessSettings() {
        try {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        } catch (e: Exception) {
            Toast.makeText(this, "Manually Settings > Notification Access open karein", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleNotificationListenerService() {
        val componentName = ComponentName(this, NotificationListener::class.java)
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}
