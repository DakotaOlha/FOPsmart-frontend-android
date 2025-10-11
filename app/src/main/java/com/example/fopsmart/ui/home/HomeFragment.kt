package com.example.fopsmart.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        observeTransactions()

        return root
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { transaction ->
            Toast.makeText(context, "Clicked: ${transaction.description}", Toast.LENGTH_SHORT).show()
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
            binding.textView5.text = "$ $balance"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}