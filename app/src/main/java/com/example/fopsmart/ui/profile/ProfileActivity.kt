package com.example.fopsmart.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fopsmart.R
import com.example.fopsmart.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButtonProfile.setOnClickListener {
            finish()
        }

        binding.notificationsItem.setOnClickListener {
            Toast.makeText(this, "Повідомлення", Toast.LENGTH_SHORT).show()
        }

        binding.passwordItem.setOnClickListener {
            Toast.makeText(this, "Пароль", Toast.LENGTH_SHORT).show()
        }

        binding.languageItem.setOnClickListener {
            Toast.makeText(this, "Мова", Toast.LENGTH_SHORT).show()
        }

        binding.helpItem.setOnClickListener {
            Toast.makeText(this, "Допомога", Toast.LENGTH_SHORT).show()
        }

        binding.aboutItem.setOnClickListener {
            Toast.makeText(this, "Про нас", Toast.LENGTH_SHORT).show()
        }

        binding.logoutItem.setOnClickListener {
            showLogoutDialog()
        }

        binding.deliteItem.setOnClickListener {
            showDeleteDialog()
        }

        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, if (isChecked) "Темна тема" else "Світла тема", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Вихід")
            .setMessage("Ви впевнені, що хочете вийти?")
            .setPositiveButton("Вийти") { _, _ ->
                Toast.makeText(this, "Вихід...", Toast.LENGTH_SHORT).show()
                // Реалізувати логіку виходу
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }

    private fun showDeleteDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Видалити аккаунт")
            .setMessage("Ця дія не може бути скасована. Ви впевнені?")
            .setPositiveButton("Видалити") { _, _ ->
                Toast.makeText(this, "Видалення...", Toast.LENGTH_SHORT).show()
                // Реалізувати логіку видалення
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }
}