package com.example.fopsmart.ui.stats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fopsmart.data.AnalyticsRepository
import com.example.fopsmart.data.Result
import com.example.fopsmart.data.model.DashboardData
import kotlinx.coroutines.launch

class StatsViewModel : ViewModel() {

    private val analyticsRepository = AnalyticsRepository()
    private val TAG = "StatsViewModel"

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _dashboardData = MutableLiveData<DashboardData?>()
    val dashboardData: LiveData<DashboardData?> = _dashboardData

    private var currentDays = 30

    fun setDays(days: Int) {
        currentDays = days
        Log.d(TAG, "Days changed to: $days")
    }

    fun loadDashboard(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "=== Loading dashboard ===")
            Log.d(TAG, "Days: $currentDays")

            when (val result = analyticsRepository.getDashboard(token, currentDays)) {
                is Result.Success -> {
                    val data = result.data
                    Log.d(TAG, "✓ Dashboard loaded successfully")
                    Log.d(TAG, "  Period: ${data.period.start} to ${data.period.end}")
                    Log.d(TAG, "  Income: ${data.income.totalAmount} (${data.income.totalTransactions} trans)")
                    Log.d(TAG, "  Expenses: ${data.expenses.totalAmount} (${data.expenses.totalTransactions} trans)")
                    Log.d(TAG, "  Net Income: ${data.netIncome}")
                    Log.d(TAG, "  Top categories: ${data.topCategories.size}")

                    data.topCategories.take(5).forEachIndexed { index, cat ->
                        Log.d(TAG, "    [$index] ${cat.category}: ${cat.totalSpent} (${cat.transactionCount} trans)")
                    }

                    Log.d(TAG, "  Daily trends: ${data.dailyTrends.size} days")
                    Log.d(TAG, "  Limit status: ${data.limitStatus.status}")

                    _dashboardData.value = data
                }
                is Result.Error -> {
                    Log.e(TAG, "✗ Dashboard error: ${result.exception.message}")
                    _error.value = "Помилка завантаження: ${result.exception.message}"
                }
            }

            _isLoading.value = false
            Log.d(TAG, "=== Dashboard loading completed ===")
        }
    }
}