package com.example.fopsmart.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.fopsmart.R
import com.example.fopsmart.databinding.FragmentProfileBinding

class ProfileDialogFragment : DialogFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            dismiss()
        }

        binding.notificationsItem.setOnClickListener {
            Toast.makeText(context, "Повідомлення", Toast.LENGTH_SHORT).show()
        }

        binding.passwordItem.setOnClickListener {
            Toast.makeText(context, "Пароль", Toast.LENGTH_SHORT).show()
        }

        binding.languageItem.setOnClickListener {
            Toast.makeText(context, "Мова", Toast.LENGTH_SHORT).show()
        }

        binding.helpItem.setOnClickListener {
            Toast.makeText(context, "Допомога", Toast.LENGTH_SHORT).show()
        }

        binding.aboutItem.setOnClickListener {
            Toast.makeText(context, "Про нас", Toast.LENGTH_SHORT).show()
        }

        binding.logoutItem.setOnClickListener {
            showLogoutDialog()
        }

        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(context, if (isChecked) "Темна тема" else "Світла тема", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Вихід")
            .setMessage("Ви впевнені, що хочете вийти?")
            .setPositiveButton("Вийти") { _, _ ->
                Toast.makeText(context, "Вихід...", Toast.LENGTH_SHORT).show()
                // Реалізуй логіку виходу
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}