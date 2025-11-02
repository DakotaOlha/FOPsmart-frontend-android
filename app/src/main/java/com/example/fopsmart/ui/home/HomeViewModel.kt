package com.example.fopsmart.ui.home

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fopsmart.data.Result
import com.example.fopsmart.data.TransactionRepository
import com.example.fopsmart.data.model.Transaction
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val transactionRepository = TransactionRepository()

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _totalBalance = MutableLiveData<String>()
    val totalBalance: LiveData<String> = _totalBalance

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isBankConnection = MutableLiveData<Boolean>()
    val isBankConnection: LiveData<Boolean> = _isBankConnection

    private val _bankConnectionError = MutableLiveData<String?>()
    val bankConnectionError: LiveData<String?> = _bankConnectionError

    fun checkBankConnectionStatus(token: String) {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = transactionRepository.checkBankStatus(token)) {
                is Result.Success -> {
                    val isBankConnected = result.data
                    _isBankConnection.value = isBankConnected
                    _bankConnectionError.value = null

                    if (isBankConnected) {
                        loadTransactions(token)
                    } else {
                        _transactions.value = emptyList()
                        _totalBalance.value = "₴ 0"
                        _isLoading.value = false
                    }
                }
                is Result.Error -> {
                    _isBankConnection.value = false
                    _bankConnectionError.value = result.exception.message ?: "Невідома помилка при перевірці банку"
                    _transactions.value = emptyList()
                    _totalBalance.value = "₴ 0"
                    _isLoading.value = false
                }
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun loadTransactions(token: String) {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = transactionRepository.getTransactions(token)) {
                is Result.Success -> {
                    _transactions.value = result.data
                    calculateBalance(result.data)
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception.message ?: "Помилка при завантаженні транзакцій"
                }
            }

            _isLoading.value = false
        }
    }

    @SuppressLint("DefaultLocale")
    private fun calculateBalance(transactions: List<Transaction>) {
        val balance = transactions.sumOf { it.amount }

        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))

        val decimalFormatSymbols = (currencyFormatter as? java.text.DecimalFormat)?.decimalFormatSymbols
        decimalFormatSymbols?.currencySymbol = ""
        decimalFormatSymbols?.groupingSeparator = ' '
        (currencyFormatter as? java.text.DecimalFormat)?.decimalFormatSymbols = decimalFormatSymbols
        (currencyFormatter as? java.text.DecimalFormat)?.maximumFractionDigits = 0

        _totalBalance.value = "₴ ${currencyFormatter.format(balance)}"
    }

    fun loadMockTransactions() {
        val mockTransactions = listOf(
            Transaction(
                id = "1",
                date = "2023-10-15T14:30:00Z",
                amount = -1250.00,
                currency = "UAH",
                category = "Паливо",
                description = "Заправка АЗС OKKO",
                type = "expense",
                bankAccountId = "456e4567-e89b-12d3-a456-426614174000"
            ),
            Transaction(
                id = "2",
                date = "2023-10-14T10:20:00Z",
                amount = 5000.00,
                currency = "UAH",
                category = "Зарплата",
                description = "Зарплата за жовтень",
                type = "income",
                bankAccountId = "456e4567-e89b-12d3-a456-426614174000"
            ),
            Transaction(
                id = "3",
                date = "2023-10-13T18:45:00Z",
                amount = -450.00,
                currency = "UAH",
                category = "Їжа",
                description = "Silpo",
                type = "expense",
                bankAccountId = "456e4567-e89b-12d3-a456-426614174000"
            ),
            Transaction(
                id = "4",
                date = "2023-10-12T09:15:00Z",
                amount = -1250.00,
                currency = "UAH",
                category = "Паливо",
                description = "Заправка АЗС WOG",
                type = "expense",
                bankAccountId = "456e4567-e89b-12d3-a456-426614174000"
            ),
            Transaction(
                id = "5",
                date = "2023-10-11T16:45:00Z",
                amount = 5000.00,
                currency = "UAH",
                category = "Бонус",
                description = "Годовой бонус",
                type = "income",
                bankAccountId = "456e4567-e89b-12d3-a456-426614174000"
            ),
            Transaction(
                id = "6",
                date = "2023-10-10T12:30:00Z",
                amount = -450.00,
                currency = "UAH",
                category = "Їжа",
                description = "Metro",
                type = "expense",
                bankAccountId = "456e4567-e89b-12d3-a456-426614174000"
            )
        )

        _transactions.value = mockTransactions
        calculateBalance(mockTransactions)
        _isBankConnection.value = true
    }
}