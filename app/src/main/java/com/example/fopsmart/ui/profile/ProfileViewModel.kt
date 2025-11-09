package com.example.fopsmart.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fopsmart.data.ProfileRepository
import com.example.fopsmart.data.model.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val TAG = "ProfileViewModel"

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
                Log.d(TAG, "Завантаження профілю з токеном: ${token.take(20)}...")
                val response = repository.getProfile(token)

                Log.d(TAG, "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d(TAG, "Profile body: $body")
                    _profileData.value = body

                    if (body != null) {
                        Log.d(TAG, "Профіль успішно завантажено: ${body.firstName} ${body.lastName}")
                    } else {
                        Log.w(TAG, "Тіло відповіді порожнє")
                        _error.value = "Помилка: порожня відповідь сервера"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Помилка завантаження профілю: ${response.code()}")
                    Log.e(TAG, "Error body: $errorBody")
                    _error.value = "Помилка завантаження профілю: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception в loadProfile", e)
                _error.value = "Помилка мережі: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadFopConfig(token: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Завантаження FOP конфігурації")
                val response = repository.getFopConfig(token)

                Log.d(TAG, "FOP Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d(TAG, "FOP config body: $body")
                    _fopConfig.value = body

                    if (body != null) {
                        Log.d(TAG, "FOP конфігурація завантажена: group=${body.fopGroup}, tax=${body.taxSystem}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Помилка завантаження FOP: ${response.code()}")
                    Log.e(TAG, "Error body: $errorBody")
                    _error.value = "Помилка завантаження FOP: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception в loadFopConfig", e)
                _error.value = "Помилка мережі: ${e.message}"
            }
        }
    }

    fun updateProfile(token: String, firstName: String, lastName: String, email: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                Log.d(TAG, "Оновлення профілю: $firstName $lastName, $email")
                val response = repository.updateProfile(token, firstName, lastName, email)

                Log.d(TAG, "Update response code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d(TAG, "Update success: ${body?.message}")
                    _updateSuccess.value = body?.message ?: "Профіль успішно оновлено"

                    loadProfile(token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Помилка оновлення: ${response.code()}")
                    Log.e(TAG, "Error body: $errorBody")
                    _error.value = "Помилка оновлення: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception в updateProfile", e)
                _error.value = "Помилка мережі: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun changePassword(token: String, currentPassword: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                Log.d(TAG, "Зміна паролю")
                val response = repository.changePassword(token, currentPassword, newPassword, confirmPassword)

                Log.d(TAG, "Password change response code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d(TAG, "Password change success: ${body?.message}")
                    _updateSuccess.value = body?.message ?: "Пароль успішно змінено"
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Помилка зміни паролю: ${response.code()}")
                    Log.e(TAG, "Error body: $errorBody")
                    _error.value = "Помилка зміни паролю: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception в changePassword", e)
                _error.value = "Помилка мережі: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateFopConfig(token: String, fopGroup: Int, taxSystem: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                Log.d(TAG, "Оновлення FOP конфігурації: group=$fopGroup, tax=$taxSystem")
                val response = repository.updateFopConfig(token, fopGroup, taxSystem)

                Log.d(TAG, "FOP update response code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d(TAG, "FOP update success: ${body?.message}")
                    _updateSuccess.value = body?.message ?: "FOP конфігурація успішно оновлена"

                    loadFopConfig(token)
                    loadProfile(token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Помилка оновлення FOP: ${response.code()}")
                    Log.e(TAG, "Error body: $errorBody")
                    _error.value = "Помилка оновлення FOP: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception в updateFopConfig", e)
                _error.value = "Помилка мережі: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteAccount(token: String, password: String, confirmation: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                Log.d(TAG, "Видалення акаунта")
                val response = repository.deleteAccount(token, password, confirmation)

                Log.d(TAG, "Delete response code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d(TAG, "Delete success: ${body?.message}")
                    _updateSuccess.value = body?.message ?: "Акаунт успішно видалено"
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Помилка видалення: ${response.code()}")
                    Log.e(TAG, "Error body: $errorBody")
                    _error.value = "Помилка видалення: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception в deleteAccount", e)
                _error.value = "Помилка мережі: ${e.message}"
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