package com.example.fopsmart.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fopsmart.data.ProfileRepository
import com.example.fopsmart.data.model.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _profileData = MutableLiveData<ProfileResponse?>()
    val profileData: LiveData<ProfileResponse?> = _profileData

    private val _fopConfig = MutableLiveData<FopConfigResponse?>()
    val fopConfig: LiveData<FopConfigResponse?> = _fopConfig

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _updateSuccess = MutableLiveData<String?>()
    val updateSuccess: LiveData<String?> = _updateSuccess

    fun loadProfile(token: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getProfile(token)
                if (response.isSuccessful) {
                    _profileData.value = response.body()
                } else {
                    _error.value = "Помилка завантаження профілю: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Помилка: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadFopConfig(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getFopConfig(token)
                if (response.isSuccessful) {
                    _fopConfig.value = response.body()
                } else {
                    _error.value = "Помилка завантаження FOP: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Помилка: ${e.message}"
            }
        }
    }

    fun updateProfile(token: String, firstName: String, lastName: String, email: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.updateProfile(token, firstName, lastName, email)
                if (response.isSuccessful) {
                    _updateSuccess.value = response.body()?.message
                    loadProfile(token) // Перезавантажити дані
                } else {
                    _error.value = "Помилка оновлення: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Помилка: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun changePassword(token: String, currentPassword: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.changePassword(token, currentPassword, newPassword, confirmPassword)
                if (response.isSuccessful) {
                    _updateSuccess.value = response.body()?.message
                } else {
                    _error.value = "Помилка зміни паролю: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Помилка: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateFopConfig(token: String, fopGroup: Int, taxSystem: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.updateFopConfig(token, fopGroup, taxSystem)
                if (response.isSuccessful) {
                    _updateSuccess.value = response.body()?.message
                    loadFopConfig(token) // Перезавантажити дані
                } else {
                    _error.value = "Помилка оновлення FOP: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Помилка: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteAccount(token: String, password: String, confirmation: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.deleteAccount(token, password, confirmation)
                if (response.isSuccessful) {
                    _updateSuccess.value = response.body()?.message
                } else {
                    _error.value = "Помилка видалення: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Помилка: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _updateSuccess.value = null
    }
}