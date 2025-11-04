package com.example.fopsmart.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fopsmart.R
import com.example.fopsmart.databinding.ActivityProfileBinding
import com.example.fopsmart.utils.LanguageManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var languageManager: LanguageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageManager = LanguageManager(this)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButtonProfile.setOnClickListener {
            finish()
        }

        binding.notificationsItem.setOnClickListener {
            Toast.makeText(this, getString(R.string.profile_notifications), Toast.LENGTH_SHORT).show()
        }

        binding.passwordItem.setOnClickListener {
            Toast.makeText(this, getString(R.string.profile_password), Toast.LENGTH_SHORT).show()
        }

        binding.languageItem.setOnClickListener {
            showLanguageDialog()
        }

        binding.helpItem.setOnClickListener {
            Toast.makeText(this, getString(R.string.profile_help), Toast.LENGTH_SHORT).show()
        }

        binding.aboutItem.setOnClickListener {
            Toast.makeText(this, getString(R.string.profile_about_us), Toast.LENGTH_SHORT).show()
        }

        binding.logoutItem.setOnClickListener {
            showLogoutDialog()
        }

        binding.deliteItem.setOnClickListener {
            showDeleteDialog()
        }

        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val themeMessage = if (isChecked) {
                getString(R.string.profile_dark_theme_on)
            } else {
                getString(R.string.profile_dark_theme_off)
            }
            Toast.makeText(this, themeMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Українська", "English")
        val languageCodes = arrayOf(LanguageManager.UKRAINIAN, LanguageManager.ENGLISH)
        val currentLanguage = languageManager.getCurrentLanguage()

        val checkedItem = when (currentLanguage) {
            LanguageManager.UKRAINIAN -> 0
            LanguageManager.ENGLISH -> 1
            else -> 0
        }

        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_language_title))
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                val selectedLanguage = languageCodes[which]
                languageManager.setLanguage(selectedLanguage)

                val toastMessage = getString(R.string.toast_language_changed, languages[which])
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()

                recreate()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel), null)
            .show()
    }

    private fun showLogoutDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_logout_title))
            .setMessage(getString(R.string.dialog_logout_message))
            .setPositiveButton(getString(R.string.dialog_button_logout)) { _, _ ->
                Toast.makeText(this, getString(R.string.toast_logging_out), Toast.LENGTH_SHORT).show()
                // Реалізувати логіку виходу
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel), null)
            .show()
    }

    private fun showDeleteDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(getString(R.string.dialog_delete_message))
            .setPositiveButton(getString(R.string.dialog_button_delete)) { _, _ ->
                Toast.makeText(this, getString(R.string.toast_deleting), Toast.LENGTH_SHORT).show()
                // Реалізувати логіку видалення
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel), null)
            .show()
    }
}