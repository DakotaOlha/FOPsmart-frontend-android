package com.example.fopsmart.ui.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fopsmart.data.AnalyticsRepository
import com.example.fopsmart.data.Result
import com.example.fopsmart.data.model.ComparisonData
import com.example.fopsmart.data.model.DashboardData
import com.example.fopsmart.data.model.LimitUtilization
import com.example.fopsmart.data.model.SpendingTrend
import kotlinx.coroutines.launch

class StatsViewModel : ViewModel() {

    private val analyticsRepository = AnalyticsRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _dashboardData = MutableLiveData<DashboardData?>()
    val dashboardData: LiveData<DashboardData?> = _dashboardData

    private val _spendingTrends = MutableLiveData<List<SpendingTrend>?>()
    val spendingTrends: LiveData<List<SpendingTrend>?> = _spendingTrends

    private val _incomeVsExpenses = MutableLiveData<List<ComparisonData>?>()
    val incomeVsExpenses: LiveData<List<ComparisonData>?> = _incomeVsExpenses

    private val _limitUtilization = MutableLiveData<LimitUtilization?>()
    val limitUtilization: LiveData<LimitUtilization?> = _limitUtilization

    private var currentPeriod = "month"

    fun setPeriod(period: String) {
        currentPeriod = period
    }

    fun loadAllAnalytics(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Завантаження дашборду
            when (val result = analyticsRepository.getDashboard(token, 30)) {
                is Result.Success -> _dashboardData.value = result.data
                is Result.Error -> _error.value = result.exception.message
            }

            // Завантаження трендів витрат
            when (val result = analyticsRepository.getSpendingTrends(token, currentPeriod)) {
                is Result.Success -> _spendingTrends.value = result.data.trends
                is Result.Error -> _error.value = result.exception.message
            }

            // Завантаження порівняння доходів та витрат
            when (val result = analyticsRepository.getIncomeVsExpenses(token, currentPeriod, 12)) {
                is Result.Success -> _incomeVsExpenses.value = result.data.data
                is Result.Error -> _error.value = result.exception.message
            }

            // Завантаження використання ліміту
            when (val result = analyticsRepository.getLimitUtilization(token)) {
                is Result.Success -> _limitUtilization.value = result.data
                is Result.Error -> _error.value = result.exception.message
            }

            _isLoading.value = false
        }
    }
}