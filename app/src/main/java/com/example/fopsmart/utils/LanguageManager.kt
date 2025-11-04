package com.example.fopsmart.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

class LanguageManager(private val context: Context) {

    companion object {
        private const val PREF_NAME = "LanguagePrefs"
        private const val LANGUAGE_KEY = "selected_language"
        const val UKRAINIAN = "uk"
        const val ENGLISH = "en"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getCurrentLanguage(): String {
        val savedLanguage = sharedPreferences.getString(LANGUAGE_KEY, null)
        return savedLanguage ?: UKRAINIAN
    }

    fun setLanguage(languageCode: String) {
        sharedPreferences.edit().putString(LANGUAGE_KEY, languageCode).apply()
        applyLanguage(languageCode)
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getLanguageName(languageCode: String): String {
        return when (languageCode) {
            UKRAINIAN -> "Українська"
            ENGLISH -> "English"
            else -> "Unknown"
        }
    }
}