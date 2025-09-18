package com.example.fopsmart.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fopsmart.data.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: RegisterRepository = RegisterRepository()
) : ViewModel() {

    fun register(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.register(email, password)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Помилка: ${response.code()}")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Невідома помилка")
            }
        }
    }
}
