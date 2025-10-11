package com.example.fopsmart.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fopsmart.DescActivity
import com.example.fopsmart.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        val intent = if (isLoggedIn) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, DescActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}