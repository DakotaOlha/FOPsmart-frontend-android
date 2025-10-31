package com.example.fopsmart.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fopsmart.data.model.ChatMessage
import com.example.fopsmart.data.model.ChatRequest
import com.example.fopsmart.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val apiService = RetrofitClient.aiApi

    fun sendMessage(query: String) {
        val userMessage = ChatMessage(query, isFromUser = true)
        _messages.value = _messages.value!! + userMessage

        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = ChatRequest(query = query, user_id = 1)
                val response = apiService.postChat(request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val botResponse = ChatMessage(response.body()!!.answer, isFromUser = false)
                        _messages.value = _messages.value!! + botResponse
                    } else {
                        val errorMsg = "Помилка: ${response.message()}"
                        _messages.value = _messages.value!! + ChatMessage(errorMsg, isFromUser = false)
                    }
                }
            }
            catch (e: java.net.SocketTimeoutException){
                withContext(Dispatchers.Main) {
                    _messages.value = _messages.value!! + ChatMessage("Тайм-аут: Сервер не відповідає. Спробуйте ще раз.", isFromUser = false)
                }
            }
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMsg = "Помилка мережі: ${e.message}"
                    _messages.value = _messages.value!! + ChatMessage(errorMsg, isFromUser = false)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }
}