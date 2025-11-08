package com.example.fopsmart.ui.report

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fopsmart.data.ReportRepository
import com.example.fopsmart.data.model.ReportGenerateRequest
import com.example.fopsmart.data.model.ReportPreview
import com.example.fopsmart.data.model.ReportPreviewRequest
import com.example.fopsmart.data.model.ReportType
import com.example.fopsmart.data.Result
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class ReportViewModel : ViewModel() {
    private val reportsRepository = ReportRepository()
    private val TAG = "ReportsViewModel"

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _reportTypes = MutableLiveData<List<ReportType>>()
    val reportTypes: LiveData<List<ReportType>> = _reportTypes

    private val _reportPreview = MutableLiveData<ReportPreview?>()
    val reportPreview: LiveData<ReportPreview?> = _reportPreview

    private val _generatedReport = MutableLiveData<ResponseBody?>()
    val generatedReport: LiveData<ResponseBody?> = _generatedReport

    fun loadReportTypes(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "=== Loading report types ===")

            when (val result = reportsRepository.getReportTypes(token)) {
                is Result.Success -> {
                    val types = result.data.types
                    Log.d(TAG, "✓ Report types loaded: ${types.size}")
                    types.forEach { type ->
                        Log.d(TAG, "  Type: ${type.id} - ${type.name}")
                    }
                    _reportTypes.value = types
                }
                is Result.Error -> {
                    Log.e(TAG, "✗ Error: ${result.exception.message}")
                    _error.value = "Помилка завантаження типів звітів: ${result.exception.message}"
                }
            }

            _isLoading.value = false
        }
    }

    fun loadPreview(token: String, type: String, startDate: String?, endDate: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "=== Loading preview ===")
            Log.d(TAG, "Type: $type, Period: $startDate - $endDate")

            val request = ReportPreviewRequest(
                startDate = startDate,
                endDate = endDate
            )

            when (val result = reportsRepository.getReportPreview(token, request)) {
                is Result.Success -> {
                    val preview = result.data
                    Log.d(TAG, "✓ Preview loaded")
                    Log.d(TAG, "  User: ${preview.user.firstName} ${preview.user.lastName}")
                    Log.d(TAG, "  Period: ${preview.period.from} - ${preview.period.to}")
                    Log.d(TAG, "  Income: ${preview.summary.income}")
                    Log.d(TAG, "  Expenses: ${preview.summary.expenses}")
                    Log.d(TAG, "  Net: ${preview.summary.netIncome}")
                    _reportPreview.value = preview
                }
                is Result.Error -> {
                    Log.e(TAG, "✗ Error: ${result.exception.message}")
                    _error.value = "Помилка завантаження попереднього перегляду: ${result.exception.message}"
                }
            }

            _isLoading.value = false
        }
    }

    fun generateReport(
        token: String,
        type: String,
        startDate: String?,
        endDate: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "=== Generating report ===")
            Log.d(TAG, "Type: $type, Format: pdf")
            Log.d(TAG, "Period: $startDate - $endDate")

            val request = ReportGenerateRequest(
                type = type,
                startDate = startDate,
                endDate = endDate
            )

            when (val result = reportsRepository.generateReport(token, request)) {
                is Result.Success -> {
                    Log.d(TAG, "✓ Report generated successfully")
                    _generatedReport.value = result.data
                }
                is Result.Error -> {
                    Log.e(TAG, "✗ Error: ${result.exception.message}")
                    _error.value = "Помилка генерації звіту: ${result.exception.message}"
                }
            }

            _isLoading.value = false
        }
    }
}