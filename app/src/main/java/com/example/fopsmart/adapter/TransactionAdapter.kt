package com.example.fopsmart.adapter

import androidx.fragment.app.FragmentActivity
import com.example.fopsmart.ui.transaction.TransactionDetailDialogFragment
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fopsmart.R
import com.example.fopsmart.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TransactionAdapter(
    private val onTransactionClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view, onTransactionClick)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(
        itemView: View,
        private val onTransactionClick: (Transaction) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val ivIcon: ImageView = itemView.findViewById(R.id.ivTransactionIcon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTransactionTitle)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvTransactionCategory)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)

        fun bind(transaction: Transaction) {
            tvTitle.text = transaction.description ?: "Транзакція"

            val formattedDate = formatDate(transaction.date)
            val categoryText = transaction.category ?: "Інше"
            tvCategory.text = "$categoryText • $formattedDate"

            val formattedAmount = formatAmount(transaction)
            tvAmount.text = formattedAmount

            ivIcon.setImageResource(getCategoryIcon(transaction.category ?: "Інше"))

            when (transaction.getTransactionType()) {
                Transaction.TransactionType.INCOME -> {
                    tvAmount.setTextColor(Color.parseColor("#4CAF50"))
                }
                Transaction.TransactionType.EXPENSE -> {
                    tvAmount.setTextColor(Color.parseColor("#F44336"))
                }
            }

            itemView.setOnClickListener {
                openTransactionDetail(transaction)
            }
        }

        private fun openTransactionDetail(transaction: Transaction) {
            val dialog = TransactionDetailDialogFragment.newInstance(transaction)
            val fragmentManager = (itemView.context as? androidx.fragment.app.FragmentActivity)?.supportFragmentManager
            fragmentManager?.let {
                dialog.show(it, "transaction_detail")
            }
        }

        private fun formatAmount(transaction: Transaction): String {
            val absAmount = transaction.getAbsoluteAmount()
            val currency = transaction.currency ?: "UAH"

            val sign = if (transaction.amount > 0) "+" else "-"
            return "$sign${String.format("%.2f", absAmount)} $currency"
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(dateString)

                val now = Date()
                val diffInMillis = now.time - (date?.time ?: 0)
                val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

                when {
                    diffInDays == 0L -> "Сьогодні"
                    diffInDays == 1L -> "Вчора"
                    diffInDays < 7 -> "$diffInDays дні тому"
                    else -> {
                        val outputFormat = SimpleDateFormat("dd MMM", Locale("uk"))
                        date?.let { outputFormat.format(it) } ?: dateString
                    }
                }
            } catch (e: Exception) {
                dateString
            }
        }

        private fun getCategoryIcon(category: String): Int {
            return when (category.lowercase()) {
                "паливо" -> R.drawable.img_account
                "їжа", "продукти" -> R.drawable.img_account
                "транспорт" -> R.drawable.img_account
                "розваги" -> R.drawable.img_account
                "здоров'я" -> R.drawable.img_account
                "комунальні" -> R.drawable.img_account
                "таксі" -> R.drawable.img_account
                "зв'язок" -> R.drawable.img_account
                "банки" -> R.drawable.img_account
                else -> R.drawable.img_account
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}