package com.example.fopsmart.ui.home

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.fopsmart.R
import com.example.fopsmart.adapter.TransactionAdapter
import com.example.fopsmart.databinding.FragmentHomeBinding
import com.example.fopsmart.ui.profile.ProfileActivity
import com.google.android.material.textfield.TextInputEditText

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private var activeFilterButton: Button? = null

    // Для автооновлення
    private val autoRefreshHandler = Handler(Looper.getMainLooper())
    private val AUTO_REFRESH_INTERVAL = 5 * 60 * 1000L // 5 хвилин
    private var isAutoRefreshEnabled = true

    private val autoRefreshRunnable = object : Runnable {
        override fun run() {
            if (isAutoRefreshEnabled) {
                refreshTransactions(showLoading = false)
                autoRefreshHandler.postDelayed(this, AUTO_REFRESH_INTERVAL)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireContext().getSharedPreferences("app_prefs", 0)

        setupRecyclerView()
        setupSwipeRefresh()
        observeTransactions()
        observeBankConnection()
        observeLoading()
        setupConnectBankButton()
        setupFilterButtons()
        setupProfileClickListener()

        checkBankStatusOnFragmentLoad()

        return root
    }

    override fun onResume() {
        super.onResume()
        // Оновлюємо транзакції при поверненні до фрагмента
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null && token.isNotBlank() && homeViewModel.isBankConnection.value == true) {
            refreshTransactions(showLoading = false)
        }

        // Запускаємо автооновлення
        startAutoRefresh()
    }

    override fun onPause() {
        super.onPause()
        // Зупиняємо автооновлення коли фрагмент не видимий
        stopAutoRefresh()
    }

    private fun setupSwipeRefresh() {
        // Якщо у вас є SwipeRefreshLayout в XML
        binding.swipeRefreshLayout?.setOnRefreshListener {
            refreshTransactions(showLoading = true)
        }
    }

    private fun refreshTransactions(showLoading: Boolean = true) {
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null && token.isNotBlank()) {
            homeViewModel.loadTransactions(token)
            if (!showLoading) {
                Log.d(TAG, "Автооновлення транзакцій")
            }
        }
    }

    private fun startAutoRefresh() {
        isAutoRefreshEnabled = true
        autoRefreshHandler.postDelayed(autoRefreshRunnable, AUTO_REFRESH_INTERVAL)
        Log.d(TAG, "Автооновлення увімкнено (кожні ${AUTO_REFRESH_INTERVAL / 1000 / 60} хв)")
    }

    private fun stopAutoRefresh() {
        isAutoRefreshEnabled = false
        autoRefreshHandler.removeCallbacks(autoRefreshRunnable)
        Log.d(TAG, "Автооновлення зупинено")
    }

    private fun setupProfileClickListener() {
        binding.accountButton.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupFilterButtons() {
        val filterOptions = listOf("Усі", "Витрати", "Доходи")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, filterOptions)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterTypeSpinner.adapter = adapter

        binding.filterTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (activeFilterButton != null) {
                    updateFilterButtonStyle(activeFilterButton!!, false)
                    activeFilterButton = null
                }

                (parent?.getChildAt(0) as? TextView)?.setTextColor(resources.getColor(R.color.black, null))

                val selected = filterOptions[position]
                when (selected) {
                    "Усі" -> homeViewModel.filterByType("all")
                    "Витрати" -> homeViewModel.filterByType("expense")
                    "Доходи" -> homeViewModel.filterByType("income")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.filterCategories.setOnClickListener {
            setActiveFilter(binding.filterCategories)
            showCategoriesFilter()
        }

        binding.filterPeriod.setOnClickListener {
            setActiveFilter(binding.filterPeriod)
            showDatePickerFilter()
        }
    }

    private fun setActiveFilter(button: Button) {
        if (activeFilterButton != null) {
            updateFilterButtonStyle(activeFilterButton!!, false)
        }
        activeFilterButton = button
        updateFilterButtonStyle(button, true)
    }

    private fun updateFilterButtonStyle(button: Button, isActive: Boolean) {
        if (isActive) {
            button.setBackgroundResource(R.drawable.btn_filter_active)
            button.setTextColor(resources.getColor(R.color.white, null))
        } else {
            button.setBackgroundResource(R.drawable.btn_filter_inactive)
            button.setTextColor(resources.getColor(R.color.black, null))
        }
    }

    private fun showCategoriesFilter() {
        Toast.makeText(requireContext(), "Фільтр категорій - у розробці", Toast.LENGTH_SHORT).show()
    }

    private fun showDatePickerFilter() {
        Toast.makeText(requireContext(), "Фільтр періоду - у розробці", Toast.LENGTH_SHORT).show()
    }

    private fun checkBankStatusOnFragmentLoad() {
        val token = sharedPreferences.getString("auth_token", null)
        Log.d(TAG, "Token: $token")

        if (token != null && token.isNotBlank()) {
            homeViewModel.checkBankConnectionStatus(token)
        } else {
            homeViewModel.loadMockTransactions()
            Toast.makeText(
                requireContext(),
                "Токен не знайдений, показуються тестові дані",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { transaction ->
            Toast.makeText(
                context,
                "Клік: ${transaction.description}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    private fun observeTransactions() {
        homeViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
            // Зупиняємо SwipeRefresh якщо він був активний
            binding.swipeRefreshLayout?.isRefreshing = false
        }

        homeViewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            binding.textView5.text = balance
        }
    }

    private fun observeBankConnection() {
        homeViewModel.isBankConnection.observe(viewLifecycleOwner) { isBankConnected ->
            updateUIBasedOnBankConnection(isBankConnected)
        }

        homeViewModel.bankConnectionError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(
                    context,
                    "Помилка підключення: $error",
                    Toast.LENGTH_SHORT
                ).show()
                binding.swipeRefreshLayout?.isRefreshing = false
            }
        }

        homeViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, "Помилка: $error", Toast.LENGTH_SHORT).show()
                binding.swipeRefreshLayout?.isRefreshing = false
            }
        }
    }

    private fun observeLoading() {
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Показуємо SwipeRefresh при завантаженні
            if (isLoading && binding.swipeRefreshLayout?.isRefreshing == false) {
                binding.swipeRefreshLayout?.isRefreshing = true
            }
        }
    }

    private fun updateUIBasedOnBankConnection(isBankConnected: Boolean) {
        if (isBankConnected) {
            binding.bankNotConnectedContainer.visibility = View.GONE
            binding.recyclerViewTransactions.visibility = View.VISIBLE

        } else {
            binding.bankNotConnectedContainer.visibility = View.VISIBLE
            binding.recyclerViewTransactions.visibility = View.GONE
        }
    }

    private fun setupConnectBankButton() {
        binding.connectBankButton.setOnClickListener {
            showConnectBankDialog()
        }
    }

    private fun showConnectBankDialog() {

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_connect_bank, null)
        val editTextToken = dialogView.findViewById<TextInputEditText>(R.id.editTextToken)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Підключення банку")
        builder.setView(dialogView)

        builder.setPositiveButton("Підключити") { dialog, _ ->
            val monoToken = editTextToken.text.toString().trim()
            val userToken = sharedPreferences.getString("auth_token", null)

            if (monoToken.isNotEmpty()) {
                if (userToken != null && userToken.isNotBlank()) {
                    homeViewModel.connectMonobank(userToken, monoToken)
                    Toast.makeText(
                        requireContext(),
                        "Підключення Монобанку...",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Помилка: користувач не авторизований",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "Токен не може бути порожнім", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        builder.setNegativeButton("Скасувати") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoRefresh()
        _binding = null
    }
}