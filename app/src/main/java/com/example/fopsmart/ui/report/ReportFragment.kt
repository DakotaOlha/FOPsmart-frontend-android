package com.example.fopsmart.ui.report

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.fopsmart.R
import com.example.fopsmart.data.model.ReportPreview
import com.example.fopsmart.data.model.ReportType
import com.example.fopsmart.databinding.FragmentReportBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private val TAG = "ReportsFragment"

    private var reportTypes: List<ReportType> = emptyList()
    private var selectedReportType: String? = null
    private var startDate: String? = null
    private var endDate: String? = null

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd.MM.yyyy", Locale("uk", "UA"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
        loadReportTypes()
    }

    @SuppressLint("NewApi")
    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.scrollView.alpha = if (isLoading) 0.5f else 1.0f
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: $it")
            }
        }

        viewModel.reportTypes.observe(viewLifecycleOwner) { types ->
            reportTypes = types
            setupReportTypeSpinner(types)
        }

        viewModel.reportPreview.observe(viewLifecycleOwner) { preview ->
            preview?.let { displayPreview(it) }
        }

        viewModel.generatedReport.observe(viewLifecycleOwner) { responseBody ->
            responseBody?.let { saveReport(it) }
        }
    }

    private fun setupListeners() {
        // Quick period buttons
        binding.btnLastMonth.setOnClickListener {
            setLastMonth()
        }

        binding.btnLastQuarter.setOnClickListener {
            setLastQuarter()
        }

        binding.btnLastYear.setOnClickListener {
            setLastYear()
        }

        // Date pickers
        binding.tvStartDate.setOnClickListener {
            showDatePicker { date ->
                startDate = dateFormat.format(date)
                binding.tvStartDate.text = displayFormat.format(date)
            }
        }

        binding.tvEndDate.setOnClickListener {
            showDatePicker { date ->
                endDate = dateFormat.format(date)
                binding.tvEndDate.text = displayFormat.format(date)
            }
        }

        // Action buttons
        binding.btnPreview.setOnClickListener {
            loadPreview()
        }

        binding.btnGenerate.setOnClickListener {
            generateReport()
        }
    }

    private fun setupReportTypeSpinner(types: List<ReportType>) {
        val names = types.map { it.name }
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_view, names)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.reportTypeSpinner.adapter = adapter

        binding.reportTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = types[position]
                selectedReportType = type.id
                binding.tvReportDescription.text = type.description
                Log.d(TAG, "Selected report type: ${type.id} - ${type.name}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadReportTypes() {
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            Log.d(TAG, "Loading report types")
            viewModel.loadReportTypes(token)
        } else {
            Toast.makeText(requireContext(), "Токен не знайдено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPreview() {
        if (selectedReportType == null) {
            Toast.makeText(requireContext(), "Оберіть тип звіту", Toast.LENGTH_SHORT).show()
            return
        }

        if (startDate == null || endDate == null) {
            Toast.makeText(requireContext(), "Оберіть період", Toast.LENGTH_SHORT).show()
            return
        }

        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            Log.d(TAG, "Loading preview for: $selectedReportType")
            viewModel.loadPreview(token, selectedReportType!!, startDate, endDate)
        }
    }

    private fun generateReport() {
        if (selectedReportType == null) {
            Toast.makeText(requireContext(), "Оберіть тип звіту", Toast.LENGTH_SHORT).show()
            return
        }

        if (startDate == null || endDate == null) {
            Toast.makeText(requireContext(), "Оберіть період", Toast.LENGTH_SHORT).show()
            return
        }

        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            Log.d(TAG, "Generating report for: $selectedReportType")
            viewModel.generateReport(token, selectedReportType!!, startDate, endDate)
        }
    }

    private fun displayPreview(preview: ReportPreview) {
        Log.d(TAG, "Displaying preview")
        binding.previewSection.visibility = View.VISIBLE

        // User info
        binding.tvPreviewUser.text = "${preview.user.firstName} ${preview.user.lastName}"

        // Period
        val periodFrom = formatDateFromISO(preview.period.from)
        val periodTo = formatDateFromISO(preview.period.to)
        binding.tvPreviewPeriod.text = "Період: $periodFrom - $periodTo (${preview.period.days} днів)"

        // Summary
        binding.tvPreviewIncome.text = formatCurrency(preview.summary.income)
        binding.tvPreviewExpenses.text = formatCurrency(abs(preview.summary.expenses))
        binding.tvPreviewNetIncome.text = formatCurrency(preview.summary.netIncome)

        // Scroll to preview
        binding.scrollView.post {
            binding.scrollView.smoothScrollTo(0, binding.previewSection.top)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveReport(responseBody: okhttp3.ResponseBody) {
        try {
            Log.d(TAG, "Saving report to Downloads")

            val fileName = "FopSmart_Report_${System.currentTimeMillis()}.pdf"
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val resolver = requireContext().contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    responseBody.byteStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                Log.d(TAG, "Report saved successfully: $fileName")
                Toast.makeText(
                    requireContext(),
                    "Звіт збережено в Downloads/$fileName",
                    Toast.LENGTH_LONG
                ).show()
            } ?: run {
                Log.e(TAG, "Failed to create file in Downloads")
                Toast.makeText(requireContext(), "Помилка збереження файлу", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving report", e)
            Toast.makeText(requireContext(), "Помилка: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setLastMonth() {
        val calendar = Calendar.getInstance()
        endDate = dateFormat.format(calendar.time)
        binding.tvEndDate.text = displayFormat.format(calendar.time)

        calendar.add(Calendar.MONTH, -1)
        startDate = dateFormat.format(calendar.time)
        binding.tvStartDate.text = displayFormat.format(calendar.time)

        Log.d(TAG, "Period set to last month: $startDate - $endDate")
    }

    private fun setLastQuarter() {
        val calendar = Calendar.getInstance()
        endDate = dateFormat.format(calendar.time)
        binding.tvEndDate.text = displayFormat.format(calendar.time)

        calendar.add(Calendar.MONTH, -3)
        startDate = dateFormat.format(calendar.time)
        binding.tvStartDate.text = displayFormat.format(calendar.time)

        Log.d(TAG, "Period set to last quarter: $startDate - $endDate")
    }

    private fun setLastYear() {
        val calendar = Calendar.getInstance()
        endDate = dateFormat.format(calendar.time)
        binding.tvEndDate.text = displayFormat.format(calendar.time)

        calendar.add(Calendar.YEAR, -1)
        startDate = dateFormat.format(calendar.time)
        binding.tvStartDate.text = displayFormat.format(calendar.time)

        Log.d(TAG, "Period set to last year: $startDate - $endDate")
    }

    private fun formatDateFromISO(isoDate: String): String {
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = isoFormat.parse(isoDate)
            date?.let { displayFormat.format(it) } ?: isoDate
        } catch (e: Exception) {
            isoDate.take(10) // Fallback to "yyyy-MM-dd"
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