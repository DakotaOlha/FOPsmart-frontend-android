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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
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

    private var allTransactions: List<Transaction> = emptyList()

    private var selectedType: String = "all"
    private var selectedDateFrom: String? = null
    private var selectedDateTo: String? = null
    private var selectedAccountId: Int? = null

    fun checkBankConnectionStatus(token: String) {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = transactionRepository.checkBankStatus(token)) {
                is Result.Success -> {
                    val isBankConnected = result.data
                    _isBankConnection.value = isBankConnected
                    _bankConnectionError.value = null

                    if (isBankConnected) {
                        transactionRepository.getAccountBalances(token)
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

    fun connectMonobank(token: String, monoToken: String) {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = transactionRepository.connectMonobank(token, monoToken)) {
                is Result.Success -> {
                    if (result.data) {
                        _isBankConnection.value = true
                        _bankConnectionError.value = null
                        loadTransactions(token)
                    } else {
                        _bankConnectionError.value = "Не вдалося підключити Монобанк"
                        _isLoading.value = false
                    }
                }
                is Result.Error -> {
                    _isBankConnection.value = false
                    _bankConnectionError.value = result.exception.message ?: "Помилка при підключенні Монобанку"
                    _isLoading.value = false
                }
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun loadTransactions(token: String) {
        //loadMockTransactions()
        viewModelScope.launch {
            _isLoading.value = true

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val toDate = Date()
            val fromDate = Date(System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L)) // 90 днів тому

            val dateTo = dateFormat.format(toDate)
            val dateFrom = dateFormat.format(fromDate)

            when (val result = transactionRepository.getTransactions(
                token = token,
                dateFrom = dateFrom,
                dateTo = dateTo,
                accountId = null,
                limit = 100,
                type = "all"
            )) {
                is Result.Success -> {
                    allTransactions = result.data
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

    fun filterByType(type: String) {
        selectedType = type
        applyFilters()
    }

    fun filterByDateRange(dateFrom: String?, dateTo: String?) {
        selectedDateFrom = dateFrom
        selectedDateTo = dateTo
        applyFilters()
    }

    fun filterByAccountId(accountId: Int?) {
        selectedAccountId = accountId
        applyFilters()
    }

    private fun applyFilters() {
        var filtered = allTransactions

        filtered = when (selectedType) {
            "expense" -> filtered.filter { it.amount < 0 }
            "income" -> filtered.filter { it.amount > 0 }
            else -> filtered
        }

        if (selectedDateFrom != null || selectedDateTo != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            filtered = filtered.filter { transaction ->
                val transactionDate = try {
                    dateFormat.parse(transaction.date)
                } catch (e: Exception) {
                    return@filter true
                } ?: return@filter true

                val isAfterFrom = selectedDateFrom?.let {
                    val fromDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it) ?: return@let true
                    transactionDate.after(fromDate) || transactionDate.time == fromDate.time
                } ?: true

                val isBeforeTo = selectedDateTo?.let {
                    val toDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it) ?: return@let true
                    transactionDate.before(toDate) || transactionDate.time == toDate.time
                } ?: true

                isAfterFrom && isBeforeTo
            }
        }

        _transactions.value = filtered
        calculateBalance(filtered)
    }

    // Скидання фільтрів
    fun resetFilters() {
        selectedType = "all"
        selectedDateFrom = null
        selectedDateTo = null
        selectedAccountId = null
        _transactions.value = allTransactions
        calculateBalance(allTransactions)
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