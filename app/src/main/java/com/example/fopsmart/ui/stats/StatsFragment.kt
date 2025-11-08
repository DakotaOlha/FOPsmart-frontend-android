package com.example.fopsmart.ui.stats

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fopsmart.R
import com.example.fopsmart.data.model.DashboardData
import com.example.fopsmart.databinding.FragmentStatsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private val TAG = "StatsFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPeriodSpinner()
        setupObservers()
        loadAnalytics()
    }

    private fun setupPeriodSpinner() {
        val periods = listOf("Тиждень", "Місяць", "Квартал", "Рік")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_view, periods)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.periodSpinner.adapter = adapter
        binding.periodSpinner.setSelection(1)

        binding.periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val days = when (position) {
                    0 -> 7   // Тиждень
                    1 -> 30  // Місяць
                    2 -> 90  // Квартал
                    3 -> 365 // Рік
                    else -> 30
                }
                viewModel.setDays(days)
                loadAnalytics()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.scrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error: $it")
            }
        }

        viewModel.dashboardData.observe(viewLifecycleOwner) { data ->
            data?.let {
                Log.d(TAG, "Dashboard data received: $it")
                updateDashboard(it)
            }
        }
    }

    private fun loadAnalytics() {
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            Log.d(TAG, "Loading analytics with token")
            viewModel.loadDashboard(token)
        } else {
            Toast.makeText(requireContext(), "Токен не знайдено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDashboard(data: DashboardData) {
        Log.d(TAG, "=== Updating dashboard ===")
        Log.d(TAG, "Income: ${data.income.totalAmount}")
        Log.d(TAG, "Expenses: ${data.expenses.totalAmount}")
        Log.d(TAG, "Net Income: ${data.netIncome}")
        Log.d(TAG, "Top categories: ${data.topCategories.size}")
        Log.d(TAG, "Daily trends: ${data.dailyTrends.size}")

        // Відображаємо доходи і витрати
        binding.tvTotalIncome.text = formatCurrency(data.income.totalAmount)
        binding.tvTotalExpenses.text = formatCurrency(abs(data.expenses.totalAmount))
        binding.tvNetIncome.text = formatCurrency(data.netIncome)

        // Транзакції
        binding.tvIncomeTransactions.text = "${data.income.totalTransactions} транзакцій"
        binding.tvExpensesTransactions.text = "${data.expenses.totalTransactions} транзакцій"

        // Топ категорії витрат
        updateTopCategories(data.topCategories)

        // Графіки
        updateSpendingChart(data.topCategories)
        updateComparisonChart(data.dailyTrends)

        // Ліміт
        updateLimitCard(data.limitStatus)
    }

    private fun updateTopCategories(topCategories: List<com.example.fopsmart.data.model.CategoryInfo>) {
        if (topCategories.isNotEmpty()) {
            Log.d(TAG, "Displaying ${topCategories.size} top categories")
            val top3 = topCategories.take(3)

            binding.tvTopCategory1.text = "${top3[0].category}: ${formatCurrency(abs(top3[0].totalSpent))}"
            binding.tvTopCategory1.visibility = View.VISIBLE

            if (top3.size > 1) {
                binding.tvTopCategory2.text = "${top3[1].category}: ${formatCurrency(abs(top3[1].totalSpent))}"
                binding.tvTopCategory2.visibility = View.VISIBLE
            } else {
                binding.tvTopCategory2.visibility = View.GONE
            }

            if (top3.size > 2) {
                binding.tvTopCategory3.text = "${top3[2].category}: ${formatCurrency(abs(top3[2].totalSpent))}"
                binding.tvTopCategory3.visibility = View.VISIBLE
            } else {
                binding.tvTopCategory3.visibility = View.GONE
            }
        } else {
            Log.w(TAG, "No top categories data available")
            binding.tvTopCategory1.text = "Немає даних про категорії"
            binding.tvTopCategory1.visibility = View.VISIBLE
            binding.tvTopCategory2.visibility = View.GONE
            binding.tvTopCategory3.visibility = View.GONE
        }
    }

    private fun updateSpendingChart(topCategories: List<com.example.fopsmart.data.model.CategoryInfo>) {
        // Фільтруємо категорії з нульовими сумами
        val nonZeroCategories = topCategories.filter { abs(it.totalSpent) > 0 }

        if (nonZeroCategories.isEmpty()) {
            Log.w(TAG, "All spending categories are zero, showing empty chart")
            showEmptySpendingChart()
            return
        }

        Log.d(TAG, "Updating spending chart with ${nonZeroCategories.size} categories")

        val sortedCategories = nonZeroCategories.sortedByDescending { abs(it.totalSpent) }
        val entries = sortedCategories.mapIndexed { index, category ->
            Log.d(TAG, "Category $index: ${category.category} - ${category.totalSpent}")
            BarEntry(index.toFloat(), abs(category.totalSpent).toFloat())
        }

        val dataSet = BarDataSet(entries, "Витрати по категоріях").apply {
            color = Color.parseColor("#7C4DFF")
            valueTextColor = Color.BLACK
            valueTextSize = 10f
        }

        val barData = BarData(dataSet)
        binding.spendingChart.apply {
            data = barData
            description.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(sortedCategories.map { it.category })
                granularity = 1f
                labelRotationAngle = -45f
                textSize = 10f
            }
            axisLeft.setDrawGridLines(true)
            axisRight.isEnabled = false
            legend.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }

    private fun showEmptySpendingChart() {
        binding.spendingChart.clear()
        binding.spendingChart.setNoDataText("Немає даних про витрати за обраний період")
        binding.spendingChart.invalidate()
    }

    private fun updateComparisonChart(dailyTrends: List<com.example.fopsmart.data.model.Trend>) {
        if (dailyTrends.isEmpty()) {
            showEmptyComparisonChart()
            return
        }

        Log.d(TAG, "Updating comparison chart with ${dailyTrends.size} days")

        // Беремо останні 30 днів для кращого відображення
        val recentTrends = dailyTrends.takeLast(30)

        val incomeEntries = recentTrends.mapIndexed { index, trend ->
            Entry(index.toFloat(), trend.income.toFloat())
        }

        val expenseEntries = recentTrends.mapIndexed { index, trend ->
            Entry(index.toFloat(), abs(trend.expenses).toFloat())
        }

        val incomeDataSet = LineDataSet(incomeEntries, "Доходи").apply {
            color = Color.parseColor("#4CAF50")
            lineWidth = 2.5f
            setCircleColor(Color.parseColor("#4CAF50"))
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val expenseDataSet = LineDataSet(expenseEntries, "Витрати").apply {
            color = Color.parseColor("#F44336")
            lineWidth = 2.5f
            setCircleColor(Color.parseColor("#F44336"))
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val lineData = LineData(incomeDataSet, expenseDataSet)

        // Форматуємо дати для осі X
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("dd.MM", Locale("uk", "UA"))
        val labels = recentTrends.map {
            try {
                val date = dateFormat.parse(it.date)
                date?.let { displayFormat.format(it) } ?: it.date
            } catch (e: Exception) {
                it.date.substring(8) // Беремо тільки день
            }
        }

        binding.comparisonChart.apply {
            data = lineData
            description.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(labels)
                granularity = 1f
                textSize = 8f
                labelRotationAngle = -45f
            }
            axisLeft.setDrawGridLines(true)
            axisRight.isEnabled = false
            legend.isEnabled = true
            animateX(1000)
            invalidate()
        }
    }

    private fun showEmptyComparisonChart() {
        binding.comparisonChart.clear()
        binding.comparisonChart.setNoDataText("Немає даних для порівняння")
        binding.comparisonChart.invalidate()
    }

    private fun updateLimitCard(limitStatus: com.example.fopsmart.data.model.LimitStatus) {
        Log.d(TAG, "Updating limit card:")
        Log.d(TAG, "  FOP Group: ${limitStatus.fopGroup}")
        Log.d(TAG, "  Limit: ${limitStatus.limit}")
        Log.d(TAG, "  Current: ${limitStatus.currentIncome}")
        Log.d(TAG, "  Percentage: ${limitStatus.percentage}")
        Log.d(TAG, "  Status: ${limitStatus.status}")

        if (limitStatus.limit > 0) {
            binding.limitCard.visibility = View.VISIBLE

            val currentIncome = limitStatus.currentIncome.toDoubleOrNull() ?: 0.0
            val limit = limitStatus.limit
            val remaining = limitStatus.remaining
            val percentage = limitStatus.percentage

            binding.tvLimitTotal.text = formatCurrency(limit)
            binding.tvLimitUsed.text = formatCurrency(currentIncome)
            binding.tvLimitRemaining.text = formatCurrency(abs(remaining))

            val displayPercentage = percentage.toInt().coerceIn(0, 100)

            binding.tvLimitPercentage.text = "$displayPercentage%"
            binding.progressLimit.progress = displayPercentage

            // Змінюємо колір залежно від статусу
            val color = when (limitStatus.status.lowercase()) {
                "danger" -> Color.parseColor("#F44336")  // Червоний
                "warning" -> Color.parseColor("#FF9800") // Помаранчевий
                "ok" -> Color.parseColor("#4CAF50")      // Зелений
                else -> when {
                    displayPercentage >= 90 -> Color.parseColor("#F44336")
                    displayPercentage >= 70 -> Color.parseColor("#FF9800")
                    else -> Color.parseColor("#4CAF50")
                }
            }
            binding.progressLimit.progressTintList = android.content.res.ColorStateList.valueOf(color)

            Log.d(TAG, "Limit card displayed successfully")
        } else {
            binding.limitCard.visibility = View.GONE
            Log.w(TAG, "Limit card hidden: limit=${limitStatus.limit}")
        }
    }

    private fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
        val symbols = (formatter as? java.text.DecimalFormat)?.decimalFormatSymbols
        symbols?.currencySymbol = "₴"
        (formatter as? java.text.DecimalFormat)?.decimalFormatSymbols = symbols
        return formatter.format(amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}