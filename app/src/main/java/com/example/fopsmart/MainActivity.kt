package com.example.fopsmart

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.fopsmart.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigationView
        val fabAdd: FloatingActionButton = binding.fabAdd

        navView.menu.removeItem(R.id.navigation_add)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.frame_layout) as NavHostFragment
        navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        fabAdd.setOnClickListener {
            Toast.makeText(this, "Add transaction button", Toast.LENGTH_SHORT).show()
        }
    }
}