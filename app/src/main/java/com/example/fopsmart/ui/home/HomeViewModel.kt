package com.example.fopsmart.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fopsmart.data.Result
import com.example.fopsmart.data.TransactionRepository
import com.example.fopsmart.data.model.Transaction
import kotlinx.coroutines.launch

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

    init {
        //тимчасово, поміняти на дані з апішки
        loadMockTransactions()
    }

    private fun loadMockTransactions() {
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
            )
        )

        _transactions.value = mockTransactions
        calculateBalance(mockTransactions)
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
                    _error.value = result.exception.message
                }
            }

            _isLoading.value = false
        }
    }

    @SuppressLint("DefaultLocale")
    private fun calculateBalance(transactions: List<Transaction>) {
        val balance = transactions.sumOf { it.amount }
        _totalBalance.value = String.format("%.2f", balance)
    }
}