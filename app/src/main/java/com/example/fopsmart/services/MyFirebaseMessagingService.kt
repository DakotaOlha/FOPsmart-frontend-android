package com.example.fopsmart.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.fopsmart.MainActivity
import com.example.fopsmart.R
import com.example.fopsmart.utils.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "fop_limits"
        private const val CHANNEL_NAME = "FOP Limits Notifications"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    /**
     * Викликається коли отримано новий FCM токен
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token received: ${token.take(20)}...")

        // Використовуємо NotificationHelper для обробки нового токену
        NotificationHelper.handleNewToken(this, token)
    }

    /**
     * Викликається коли отримано повідомлення
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Message received from: ${message.from}")

        // Перевірити чи є notification payload
        message.notification?.let { notification ->
            Log.d(TAG, "Notification title: ${notification.title}")
            Log.d(TAG, "Notification body: ${notification.body}")

            showNotification(
                title = notification.title ?: "FOPSmart",
                body = notification.body ?: "",
                data = message.data
            )
        }

        // Перевірити чи є data payload
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data: ${message.data}")

            // Якщо немає notification payload, показати з data
            if (message.notification == null) {
                showNotificationFromData(message.data)
            }
        }
    }

    private fun showNotificationFromData(data: Map<String, String>) {
        val type = data["type"] ?: ""
        val title = when (type) {
            "limit_warning" -> "⚠️ Попередження про ліміт"
            "limit_critical" -> "🚨 Критичний стан ліміту"
            "limit_exceeded" -> "❌ ЛІМІТ ПЕРЕВИЩЕНО"
            "sync_complete" -> "✅ Синхронізація завершена"
            else -> "FOPSmart"
        }

        showNotification(
            title = title,
            body = generateMessageFromData(data),
            data = data
        )
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent для відкриття додатку при кліку
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", data["type"])
            // Додаємо всі дані для обробки в MainActivity
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(), // Унікальний ID для кожного повідомлення
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Визначити пріоритет та колір на основі типу
        val (priority, color) = when (data["type"]) {
            "limit_exceeded" -> Pair(
                NotificationCompat.PRIORITY_HIGH,
                android.graphics.Color.parseColor("#DC2626")
            )
            "limit_critical" -> Pair(
                NotificationCompat.PRIORITY_HIGH,
                android.graphics.Color.parseColor("#F59E0B")
            )
            "limit_warning" -> Pair(
                NotificationCompat.PRIORITY_DEFAULT,
                android.graphics.Color.parseColor("#10B981")
            )
            else -> Pair(
                NotificationCompat.PRIORITY_LOW,
                android.graphics.Color.parseColor("#3B82F6")
            )
        }

        // Створити повідомлення
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(color)
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Показати повідомлення
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)

        Log.d(TAG, "Notification shown: $title")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Сповіщення про ліміти доходу ФОП"
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                enableVibration(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun generateMessageFromData(data: Map<String, String>): String {
        val type = data["type"] ?: ""
        return when (type) {
            "limit_warning", "limit_critical", "limit_exceeded" -> {
                val percentage = data["percentage"] ?: "0"
                val remaining = data["remaining"] ?: "0"
                "Ви використали $percentage% річного ліміту. Залишилось $remaining грн."
            }
            "sync_complete" -> {
                val count = data["transactionCount"] ?: "0"
                "Оброблено $count нових транзакцій з Monobank"
            }
            else -> data["message"] ?: "Нове повідомлення від FOPSmart"
        }
    }
}