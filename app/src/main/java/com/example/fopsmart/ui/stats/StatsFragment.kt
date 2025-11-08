package com.example.fopsmart.ui.stats

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fopsmart.R
import com.example.fopsmart.data.model.ComparisonData
import com.example.fopsmart.data.model.DashboardData
import com.example.fopsmart.data.model.LimitUtilization
import com.example.fopsmart.data.model.SpendingTrend
import com.example.fopsmart.databinding.FragmentStatsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.util.*

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

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
        binding.periodSpinner.setSelection(1) // Місяць за замовчуванням

        binding.periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val period = when (position) {
                    0 -> "week"
                    1 -> "month"
                    2 -> "quarter"
                    3 -> "year"
                    else -> "month"
                }
                viewModel.setPeriod(period)
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
            }
        }

        viewModel.dashboardData.observe(viewLifecycleOwner) { data ->
            data?.let { updateDashboard(it) }
        }

        viewModel.spendingTrends.observe(viewLifecycleOwner) { trends ->
            trends?.let { updateSpendingChart(it) }
        }

        viewModel.incomeVsExpenses.observe(viewLifecycleOwner) { comparison ->
            comparison?.let { updateComparisonChart(it) }
        }

        viewModel.limitUtilization.observe(viewLifecycleOwner) { limit ->
            limit?.let { updateLimitCard(it) }
        }
    }

    private fun loadAnalytics() {
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            viewModel.loadAllAnalytics(token)
        } else {
            Toast.makeText(requireContext(), "Токен не знайдено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDashboard(data: DashboardData) {
        binding.tvTotalIncome.text = formatCurrency(data.income.total)
        binding.tvTotalExpenses.text = formatCurrency(data.expenses.total)
        binding.tvNetIncome.text = formatCurrency(data.netIncome)

        // Топ категорії витрат
        val topCategories = data.expenses.topCategories ?: emptyList()
        if (topCategories.isNotEmpty()) {
            val top3 = topCategories.take(3)
            binding.tvTopCategory1.text = "${top3[0].category}: ${formatCurrency(top3[0].amount)}"
            binding.tvTopCategory2.visibility = if (top3.size > 1) View.VISIBLE else View.GONE
            binding.tvTopCategory3.visibility = if (top3.size > 2) View.VISIBLE else View.GONE

            if (top3.size > 1)
                binding.tvTopCategory2.text = "${top3[1].category}: ${formatCurrency(top3[1].amount)}"
            if (top3.size > 2)
                binding.tvTopCategory3.text = "${top3[2].category}: ${formatCurrency(top3[2].amount)}"
        } else {
            // опціонально: сховай блок, якщо даних нема
            binding.tvTopCategory1.text = "Немає даних"
            binding.tvTopCategory2.visibility = View.GONE
            binding.tvTopCategory3.visibility = View.GONE
        }
    }

    private fun updateSpendingChart(trends: List<SpendingTrend>) {
        val entries = trends.mapIndexed { index, trend ->
            BarEntry(index.toFloat(), trend.amount.toFloat())
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
                valueFormatter = IndexAxisValueFormatter(trends.map { it.category })
                granularity = 1f
                labelRotationAngle = -45f
            }
            axisLeft.setDrawGridLines(true)
            axisRight.isEnabled = false
            legend.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }

    private fun updateComparisonChart(comparison: List<ComparisonData>) {
        val incomeEntries = comparison.mapIndexed { index, data ->
            Entry(index.toFloat(), data.income.toFloat())
        }

        val expenseEntries = comparison.mapIndexed { index, data ->
            Entry(index.toFloat(), data.expenses.toFloat())
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
        binding.comparisonChart.apply {
            data = lineData
            description.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(comparison.map { it.period })
                granularity = 1f
            }
            axisLeft.setDrawGridLines(true)
            axisRight.isEnabled = false
            legend.isEnabled = true
            animateX(1000)
            invalidate()
        }
    }

    private fun updateLimitCard(limit: LimitUtilization) {
        if (limit.configured) {
            binding.limitCard.visibility = View.VISIBLE
            binding.tvLimitTotal.text = formatCurrency(limit.limit.toDouble())
            binding.tvLimitUsed.text = formatCurrency(limit.totalIncome.toDouble())
            binding.tvLimitRemaining.text = formatCurrency(limit.remaining.toDouble())
            binding.tvLimitPercentage.text = "${limit.utilizationPercentage}%"

            binding.progressLimit.progress = limit.utilizationPercentage.toInt()
        } else {
            binding.limitCard.visibility = View.GONE
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