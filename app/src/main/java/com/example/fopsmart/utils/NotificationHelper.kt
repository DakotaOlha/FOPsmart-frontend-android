package com.example.fopsmart.utils

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.fopsmart.data.model.DeviceInfo
import com.example.fopsmart.data.model.RegisterPushTokenRequest
import com.example.fopsmart.data.network.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NotificationHelper {
    private const val TAG = "NotificationHelper"

    /**
     * Реєстрація пуш-сповіщень з отриманням FCM токену
     */
    fun registerPushNotifications(context: Context, authToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Отримати FCM токен
                val fcmToken = getFcmToken(context)
                if (fcmToken != null) {
                    Log.d(TAG, "Using existing FCM token: ${fcmToken.take(20)}...")
                    sendTokenToServer(context, authToken, fcmToken)
                } else {
                    Log.d(TAG, "No existing FCM token, requesting new one...")
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val newFcmToken = task.result
                            Log.d(TAG, "New FCM Token obtained: ${newFcmToken.take(20)}...")
                            saveFcmToken(context, newFcmToken)
                            CoroutineScope(Dispatchers.IO).launch {
                                sendTokenToServer(context, authToken, newFcmToken)
                            }
                        } else {
                            Log.e(TAG, "Failed to get FCM token", task.exception)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error registering push notifications", e)
            }
        }
    }

    /**
     * Обробка нового токену з FirebaseMessagingService
     */
    fun handleNewToken(context: Context, newToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Handling new FCM token: ${newToken.take(20)}...")
                saveFcmToken(context, newToken)

                // Спробувати відправити на сервер, якщо користувач авторизований
                val authToken = getAuthToken(context)
                if (!authToken.isNullOrEmpty()) {
                    sendTokenToServer(context, authToken, newToken)
                } else {
                    Log.d(TAG, "User not authenticated, token saved for later registration")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling new token", e)
            }
        }
    }

    /**
     * Відправити токен на сервер
     */
    private suspend fun sendTokenToServer(context: Context, authToken: String, fcmToken: String) {
        try {
            val deviceInfo = DeviceInfo(
                model = Build.MODEL,
                os = "Android ${Build.VERSION.RELEASE}",
                appVersion = getAppVersion(context)
            )

            val request = RegisterPushTokenRequest(
                fcmToken = fcmToken,
                platform = "android",
                deviceInfo = deviceInfo
            )

            val response = RetrofitClient.mainApi.registerPushToken(
                token = "Bearer $authToken",
                request = request
            )

            if (response.isSuccessful) {
                Log.d(TAG, "FCM token successfully registered on server")
                // Позначити, що токен зареєстрований на сервері
                setTokenRegistered(context, true)
            } else {
                Log.e(TAG, "Failed to register FCM token: ${response.code()}")
                Log.e(TAG, "Error: ${response.errorBody()?.string()}")
                setTokenRegistered(context, false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception sending token to server", e)
            setTokenRegistered(context, false)
        }
    }

    /**
     * Деактивувати push-повідомлення
     */
    fun deactivatePushNotifications(context: Context, authToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fcmToken = getFcmToken(context)
                if (!fcmToken.isNullOrEmpty()) {
                    // TODO: Відправити запит на видалення токена з сервера
                    Log.d(TAG, "Would deactivate token: $fcmToken")
                }

                // Видалити токен локально
                clearFcmToken(context)
                FirebaseMessaging.getInstance().deleteToken()
                Log.d(TAG, "FCM token deleted and deactivated")

            } catch (e: Exception) {
                Log.e(TAG, "Error deactivating push notifications", e)
            }
        }
    }

    /**
     * Перевірити чи токен вже зареєстрований на сервері
     */
    fun isFcmTokenRegistered(context: Context): Boolean {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("fcm_token_registered", false)
    }

    /**
     * Отримати збережений FCM токен
     */
    fun getFcmToken(context: Context): String? {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("fcm_token", null)
    }

    /**
     * Отримати auth token
     */
    private fun getAuthToken(context: Context): String? {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("auth_token", null)
    }

    private fun saveFcmToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
        Log.d(TAG, "FCM token saved locally")
    }

    private fun clearFcmToken(context: Context) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("fcm_token").putBoolean("fcm_token_registered", false).apply()
    }

    private fun setTokenRegistered(context: Context, registered: Boolean) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("fcm_token_registered", registered).apply()
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }
}