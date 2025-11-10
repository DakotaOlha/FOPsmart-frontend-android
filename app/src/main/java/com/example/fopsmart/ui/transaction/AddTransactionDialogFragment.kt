package com.example.fopsmart.ui.transactions

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.fopsmart.R
import com.example.fopsmart.data.LoginRepository
import com.example.fopsmart.data.Result
import com.example.fopsmart.data.model.AddTransactionRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionDialogFragment : DialogFragment() {

    private val TAG = "AddTransactionDialog"
    private lateinit var etAmount: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText
    private lateinit var etMcc: EditText
    private lateinit var etComment: EditText

    private var selectedDate: Calendar = Calendar.getInstance()
    private var accountId: Int? = null

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val repository by lazy {
        com.example.fopsmart.data.TransactionRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupTypeDropdown()
        setupDatePicker()
        setupListeners(view)

        // Встановлюємо сьогоднішню дату за замовчуванням
        etDate.setText(dateFormat.format(selectedDate.time))

        // Завантажуємо рахунки
        loadAccounts()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Отримуємо accountId з аргументів
        arguments?.getInt("accountId")?.let {
            accountId = it
        }
    }

    private fun initViews(view: View) {
        etAmount = view.findViewById(R.id.etAmount)
        spinnerType = view.findViewById(R.id.spinnerType)
        etDescription = view.findViewById(R.id.etDescription)
        etDate = view.findViewById(R.id.etDate)
        etMcc = view.findViewById(R.id.etMcc)
        etComment = view.findViewById(R.id.etComment)
    }

    private fun setupTypeDropdown() {
        val types = arrayOf("Дохід", "Витрата")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            types
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        // За замовчуванням вибираємо "Витрата"
        spinnerType.setSelection(1)
    }

    private fun setupDatePicker() {
        etDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate.set(year, month, dayOfMonth)
                    etDate.setText(dateFormat.format(selectedDate.time))
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun setupListeners(view: View) {
        view.findViewById<View>(R.id.btnClose).setOnClickListener {
            dismiss()
        }

        view.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }

        view.findViewById<View>(R.id.btnAdd).setOnClickListener {
            if (validateInputs()) {
                addTransaction()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val amount = etAmount.text.toString().trim()
        val type = spinnerType.selectedItem?.toString() ?: ""
        val description = etDescription.text.toString().trim()
        val date = etDate.text.toString().trim()

        when {
            amount.isEmpty() -> {
                etAmount.error = "Введіть суму"
                return false
            }
            amount.toDoubleOrNull() == null -> {
                etAmount.error = "Невірний формат суми"
                return false
            }
            amount.toDouble() <= 0 -> {
                etAmount.error = "Сума повинна бути більше 0"
                return false
            }
            type.isEmpty() -> {
                Toast.makeText(context, "Оберіть тип транзакції", Toast.LENGTH_SHORT).show()
                return false
            }
            description.isEmpty() -> {
                etDescription.error = "Введіть опис"
                return false
            }
            date.isEmpty() -> {
                etDate.error = "Оберіть дату"
                return false
            }
        }

        return true
    }

    private fun loadAccounts() {
        val loginRepository = LoginRepository(requireContext())
        val token = loginRepository.getStoredToken() ?: return

        lifecycleScope.launch {
            try {
                when (val result = repository.getAccountBalances(token)) {
                    is Result.Success -> {
                        if (result.data.isNotEmpty()) {
                            accountId = result.data[0].id
                            Log.d(TAG, "Використовуємо рахунок ID: $accountId")
                        }
                    }
                    is Result.Error -> {
                        Log.e(TAG, "Помилка завантаження рахунків: ${result.exception.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception при завантаженні рахунків", e)
            }
        }
    }

    private fun addTransaction() {
        // Перевіряємо чи завантажився accountId
        if (accountId == null) {
            Toast.makeText(context, "Помилка: не знайдено рахунок", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = etAmount.text.toString().toDouble()
        val typeLabel = spinnerType.selectedItem?.toString() ?: "Витрата"
        val type = if (typeLabel == "Дохід") "income" else "expense"
        val description = etDescription.text.toString().trim()
        val date = etDate.text.toString()
        val mcc = etMcc.text.toString().trim()
        val comment = etComment.text.toString().trim()

        // Отримуємо токен
        val loginRepository = LoginRepository(requireContext())
        val token = loginRepository.getStoredToken()

        if (token == null) {
            Toast.makeText(context, "Помилка авторизації", Toast.LENGTH_SHORT).show()
            return
        }

        // Відключаємо кнопку під час завантаження
        view?.findViewById<View>(R.id.btnAdd)?.isEnabled = false

        lifecycleScope.launch {
            try {
                Log.d(TAG, "Відправка запиту на додавання транзакції")

                val request = AddTransactionRequest(
                    amount = amount,
                    description = description,
                    transactionDate = date,
                    type = type,
                    mcc = if (mcc.isNotEmpty()) mcc else null,
                    comment = if (comment.isNotEmpty()) comment else null,
                    accountId = accountId!!
                )

                when (val result = repository.addTransaction(token, request)) {
                    is Result.Success -> {
                        Log.d(TAG, "Транзакція успішно додана: ${result.data}")
                        Toast.makeText(
                            context,
                            "Транзакція успішно додана",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismiss()
                    }
                    is Result.Error -> {
                        Log.e(TAG, "Помилка додавання", result.exception)
                        Toast.makeText(
                            context,
                            "Помилка: ${result.exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        view?.findViewById<View>(R.id.btnAdd)?.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception при додаванні транзакції", e)
                Toast.makeText(
                    context,
                    "Помилка: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                view?.findViewById<View>(R.id.btnAdd)?.isEnabled = true
            }
        }
    }

    companion object {
        fun newInstance(accountId: Int? = null): AddTransactionDialogFragment {
            return AddTransactionDialogFragment().apply {
                arguments = Bundle().apply {
                    accountId?.let { putInt("accountId", it) }
                }
            }
        }
    }
}