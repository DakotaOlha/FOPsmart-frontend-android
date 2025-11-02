package com.example.fopsmart.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fopsmart.adapter.TransactionAdapter
import com.example.fopsmart.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sharedPreferences: SharedPreferences

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
        observeTransactions()
        observeBankConnection()
        observeLoading()
        setupConnectBankButton()

        checkBankStatusOnFragmentLoad()

        return root
    }

    private fun checkBankStatusOnFragmentLoad() {
        val token = sharedPreferences.getString("auth_token", null)

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
            }
        }

        homeViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, "Помилка: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeLoading() {
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Можна показати лоадер якщо потрібно
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
            Toast.makeText(
                context,
                "Функція підключення банку буде реалізована",
                Toast.LENGTH_SHORT
            ).show()

            // TODO: Реалізувати редирект на сторінку підключення Монобанку
            // val intent = Intent(requireContext(), MonobankConnectActivity::class.java)
            // startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}