package com.example.fopsmart.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fopsmart.R
import com.example.fopsmart.data.ProfileRepository
import com.example.fopsmart.data.network.RetrofitClient
import com.example.fopsmart.databinding.ActivityProfileBinding
import com.example.fopsmart.ui.login.LoginActivity
import com.example.fopsmart.utils.LanguageManager
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var languageManager: LanguageManager
    private lateinit var viewModel: ProfileViewModel
    private var authToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageManager = LanguageManager(this)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Отримати токен з SharedPreferences
        authToken = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null)

        setupViewModel()
        setupObservers()
        setupClickListeners()

        // Завантажити дані профілю
        authToken?.let {
            viewModel.loadProfile(it)
            viewModel.loadFopConfig(it)
        }
    }

    private fun setupViewModel() {
        val repository = ProfileRepository(RetrofitClient.mainApi)

        viewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(repository)
        )[ProfileViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.profileData.observe(this) { profile ->
            profile?.let {
                // Відображення імені
                val firstName = it.firstName ?: ""
                val lastName = it.lastName ?: ""

                binding.userName.text = when {
                    firstName.isNotEmpty() && lastName.isNotEmpty() -> "$firstName $lastName"
                    firstName.isNotEmpty() -> firstName
                    lastName.isNotEmpty() -> lastName
                    else -> it.email
                }

                // Відображення групи ФОП
                val groupNumber = it.fopGroup ?: 0
                val taxSystemText = when (it.taxSystem) {
                    "single_tax" -> getString(R.string.tax_single)
                    "general" -> getString(R.string.tax_general)
                    else -> ""
                }

                binding.userGroup.text = when {
                    groupNumber > 0 && taxSystemText.isNotEmpty() ->
                        "${getString(R.string.fop_group_label)}: $groupNumber, $taxSystemText"
                    groupNumber > 0 ->
                        "${getString(R.string.fop_group_label)}: $groupNumber"
                    else -> getString(R.string.fop_not_configured)
                }
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.updateSuccess.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearSuccess()
            }
        }
    }

    private fun setupClickListeners() {
        binding.backButtonProfile.setOnClickListener {
            finish()
        }

        binding.profileCard.setOnClickListener {
            showEditProfileDialog()
        }

        binding.notificationsItem.setOnClickListener {
            Toast.makeText(this, getString(R.string.profile_notifications), Toast.LENGTH_SHORT).show()
        }

        binding.passwordItem.setOnClickListener {
            showChangePasswordDialog()
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

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val firstNameInput = dialogView.findViewById<TextInputEditText>(R.id.firstNameInput)
        val lastNameInput = dialogView.findViewById<TextInputEditText>(R.id.lastNameInput)
        val emailInput = dialogView.findViewById<TextInputEditText>(R.id.emailInput)
        val fopGroupSpinner = dialogView.findViewById<Spinner>(R.id.fopGroupSpinner)
        val taxSystemSpinner = dialogView.findViewById<Spinner>(R.id.taxSystemSpinner)
        val limitInfoText = dialogView.findViewById<TextView>(R.id.limitInfoText)

        // Заповнити поточними даними профілю
        var currentEmail = ""
        viewModel.profileData.value?.let {
            firstNameInput.setText(it.firstName ?: "")
            lastNameInput.setText(it.lastName ?: "")
            emailInput.setText(it.email)
            currentEmail = it.email
        }

        // Налаштувати FOP групи
        val fopGroups = arrayOf(
            getString(R.string.fop_group_1),
            getString(R.string.fop_group_2),
            getString(R.string.fop_group_3)
        )
        val fopAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fopGroups)
        fopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fopGroupSpinner.adapter = fopAdapter

        // Налаштувати системи оподаткування
        val taxSystems = arrayOf(
            getString(R.string.tax_single),
            getString(R.string.tax_general)
        )
        val taxAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taxSystems)
        taxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taxSystemSpinner.adapter = taxAdapter

        // Встановити поточні значення FOP
        var currentFopGroup = 1
        var currentTaxSystem = "single_tax"

        viewModel.profileData.value?.let { profile ->
            currentFopGroup = profile.fopGroup ?: 1
            currentTaxSystem = profile.taxSystem ?: "single_tax"

            fopGroupSpinner.setSelection(currentFopGroup - 1)
            taxSystemSpinner.setSelection(if (currentTaxSystem == "single_tax") 0 else 1)
        }

        // Показати інформацію про ліміт
        val updateLimitInfo = { group: Int ->
            val limitAnnual = when (group) {
                1 -> 1167000
                2 -> 3028000
                3 -> 5000000
                else -> 0
            }
            val taxRate = when (group) {
                1 -> 2
                2 -> 4
                3 -> 6
                else -> 0
            }
            limitInfoText.text = getString(
                R.string.fop_limit_info_simple,
                limitAnnual,
                taxRate
            )
        }

        updateLimitInfo(currentFopGroup)

        // Оновлювати інформацію про ліміт при зміні групи
        fopGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateLimitInfo(position + 1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.edit_profile_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.dialog_button_save)) { _, _ ->
                val firstName = firstNameInput.text.toString().trim()
                val lastName = lastNameInput.text.toString().trim()
                val email = emailInput.text.toString().trim()
                val fopGroup = fopGroupSpinner.selectedItemPosition + 1
                val taxSystem = if (taxSystemSpinner.selectedItemPosition == 0) "single_tax" else "general"

                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                authToken?.let { token ->
                    // Перевірити чи змінились дані профілю
                    var profileChanged = false
                    viewModel.profileData.value?.let { profile ->
                        if (profile.firstName != firstName ||
                            profile.lastName != lastName ||
                            profile.email != email) {
                            profileChanged = true
                        }
                    }

                    // Оновити профіль якщо змінились дані
                    if (profileChanged) {
                        viewModel.updateProfile(token, firstName, lastName, email)
                    }

                    // Оновити FOP налаштування якщо змінились
                    if (fopGroup != currentFopGroup || taxSystem != currentTaxSystem) {
                        viewModel.updateFopConfig(token, fopGroup, taxSystem)
                    }

                    // Якщо нічого не змінилось, просто повідомити
                    if (!profileChanged && fopGroup == currentFopGroup && taxSystem == currentTaxSystem) {
                        Toast.makeText(this, getString(R.string.no_changes_made), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel), null)
            .show()
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)
        val currentPasswordInput = dialogView.findViewById<TextInputEditText>(R.id.currentPasswordInput)
        val newPasswordInput = dialogView.findViewById<TextInputEditText>(R.id.newPasswordInput)
        val confirmPasswordInput = dialogView.findViewById<TextInputEditText>(R.id.confirmPasswordInput)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.change_password_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.dialog_button_change)) { _, _ ->
                val currentPassword = currentPasswordInput.text.toString()
                val newPassword = newPasswordInput.text.toString()
                val confirmPassword = confirmPasswordInput.text.toString()

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(this, getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword.length < 6) {
                    Toast.makeText(this, getString(R.string.password_too_short), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                authToken?.let { token ->
                    viewModel.changePassword(token, currentPassword, newPassword, confirmPassword)
                }
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel), null)
            .show()
    }

    private fun showFopSettingsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_fop_settings, null)
        val fopGroupSpinner = dialogView.findViewById<Spinner>(R.id.fopGroupSpinner)
        val taxSystemSpinner = dialogView.findViewById<Spinner>(R.id.taxSystemSpinner)
        val limitInfo = dialogView.findViewById<TextView>(R.id.limitInfoText)

        // Налаштувати FOP групи
        val fopGroups = arrayOf(
            getString(R.string.fop_group_1),
            getString(R.string.fop_group_2),
            getString(R.string.fop_group_3)
        )
        val fopAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fopGroups)
        fopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fopGroupSpinner.adapter = fopAdapter

        // Налаштувати системи оподаткування
        val taxSystems = arrayOf(
            getString(R.string.tax_single),
            getString(R.string.tax_general)
        )
        val taxAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taxSystems)
        taxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taxSystemSpinner.adapter = taxAdapter

        // Встановити поточні значення
        viewModel.fopConfig.value?.let { config ->
            fopGroupSpinner.setSelection((config.fopGroup ?: 1) - 1)
            taxSystemSpinner.setSelection(if (config.taxSystem == "single_tax") 0 else 1)

            config.limit?.let { limit ->
                limitInfo.text = getString(
                    R.string.fop_limit_info,
                    limit.annual,
                    limit.description,
                    limit.taxRate ?: 0
                )
            }
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.fop_settings_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.dialog_button_save)) { _, _ ->
                val fopGroup = fopGroupSpinner.selectedItemPosition + 1
                val taxSystem = if (taxSystemSpinner.selectedItemPosition == 0) "single_tax" else "general"

                authToken?.let { token ->
                    viewModel.updateFopConfig(token, fopGroup, taxSystem)
                }
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel), null)
            .show()
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

        AlertDialog.Builder(this)
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
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_logout_title))
            .setMessage(getString(R.string.dialog_logout_message))
            .setPositiveButton(getString(R.string.dialog_button_logout)) { _, _ ->
                // Очистити токен
                getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply()

                // Перейти на екран логіну
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel), null)
            .show()
    }

    private fun showDeleteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_account, null)
        val passwordInput = dialogView.findViewById<TextInputEditText>(R.id.passwordInput)
        val confirmationInput = dialogView.findViewById<TextInputEditText>(R.id.confirmationInput)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(getString(R.string.dialog_delete_message))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.dialog_button_delete)) { _, _ ->
                val password = passwordInput.text.toString()
                val confirmation = confirmationInput.text.toString()

                if (password.isEmpty() || confirmation.isEmpty()) {
                    Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (confirmation != "DELETE_MY_ACCOUNT") {
                    Toast.makeText(this, getString(R.string.wrong_confirmation), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                authToken?.let { token ->
                    viewModel.deleteAccount(token, password, confirmation)

                    // Після успішного видалення
                    getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel), null)
            .show()
    }
}

// ViewModelFactory для створення ViewModel з параметрами
class ProfileViewModelFactory(private val repository: ProfileRepository) :
    ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}