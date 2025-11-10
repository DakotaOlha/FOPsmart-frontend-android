package com.example.fopsmart.ui.transaction

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.fopsmart.R
import com.example.fopsmart.data.model.Transaction
import com.example.fopsmart.databinding.DialogTransactionDetailBinding
import com.example.fopsmart.utils.CategoryMapper
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class TransactionDetailDialogFragment : DialogFragment() {

    private var _binding: DialogTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private var transaction: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.RoundedDialogStyle)

        transaction = arguments?.getSerializable("transaction") as? Transaction
            ?: throw IllegalArgumentException("Transaction is required")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transaction?.let {
            setupUI(it)
            setupClickListeners()
        }
    }

    private fun setupUI(transaction: Transaction) {

        binding.tvTransactionType.text = transaction.description ?: "Транзакція"


        val category = CategoryMapper.getUkrainianCategoryName(transaction.mcc)
        val date = formatFullDate(transaction.date)
        binding.tvCategoryDate.text = "$category • $date"


        val formattedAmount = formatAmount(transaction)
        binding.tvAmount.text = formattedAmount

        when (transaction.getTransactionType()) {
            Transaction.TransactionType.INCOME -> {
                binding.tvAmount.setTextColor(Color.parseColor("#4CAF50"))
                binding.ivTransactionIcon.setColorFilter(Color.parseColor("#4CAF50"))
            }
            Transaction.TransactionType.EXPENSE -> {
                binding.tvAmount.setTextColor(Color.parseColor("#F44336"))
                binding.ivTransactionIcon.setColorFilter(Color.parseColor("#F44336"))
            }
        }

        binding.ivTransactionIcon.setImageResource(CategoryMapper.getCategoryIcon(transaction.mcc))

        binding.tvCurrency.text = transaction.currency ?: "UAH"
        binding.tvAmount2.text = formatAmount(transaction)
        binding.tvDescription.text = transaction.description ?: "Без опису"
        binding.tvStatementId.text = transaction.id ?: "N/A"

        transaction.mcc?.let { mcc ->
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnShare.setOnClickListener {
            shareTransaction()
        }

        binding.btnCopy.setOnClickListener {
            copyToClipboard()
        }
    }

    private fun copyToClipboard() {
        transaction?.let {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val transactionText = buildTransactionText()
            val clip = ClipData.newPlainText("Транзакція", transactionText)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(
                requireContext(),
                "Скопійовано в буфер обміну",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun shareTransaction() {
        transaction?.let {
            val transactionText = buildTransactionText()
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, transactionText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Поділитися транзакцією"))
        }
    }

    private fun buildTransactionText(): String {
        transaction?.let {
            val category = CategoryMapper.getUkrainianCategoryName(it.mcc)
            return """
            Транзакція: ${it.description}
            Сума: ${formatAmount(it)} ${it.currency}
            Категорія: $category
            Дата: ${formatFullDate(it.date)}
            ID: ${it.id}
        """.trimIndent()
        }
        return "Транзакція"
    }

    private fun formatAmount(transaction: Transaction): String {
        val absAmount = transaction.getAbsoluteAmount()
        val sign = if (transaction.amount > 0) "+" else "-"
        return "$sign${String.format("%.2f", absAmount)}"
    }

    private fun formatFullDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("uk"))
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(transaction: Transaction) = TransactionDetailDialogFragment().apply {
            arguments = Bundle().apply {
                putSerializable("transaction", transaction)
            }
        }
    }
}