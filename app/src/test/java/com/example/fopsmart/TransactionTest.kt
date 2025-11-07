package com.example.fopsmart

import com.example.fopsmart.data.model.Transaction
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class TransactionTest {
    @Test
    fun testTransactionType_Income() {
        val transaction = Transaction(
            id = "1",
            date = "2024-01-01T10:00:00Z",
            amount = 1000.0,
            currency = "UAH",
            category = "Salary",
            description = "Monthly salary",
            type = "income",
            bankAccountId = "acc123"
        )
        assertEquals(Transaction.TransactionType.INCOME, transaction.getTransactionType())
    }

    @Test
    fun testTransactionType_Expense() {
        val transaction = Transaction(
            id = "2",
            date = "2024-01-01T10:00:00Z",
            amount = -500.0,
            currency = "UAH",
            category = "Food",
            description = "Groceries",
            type = "expense",
            bankAccountId = "acc123"
        )
        assertEquals(Transaction.TransactionType.EXPENSE, transaction.getTransactionType())
    }

    @Test
    fun testAbsoluteAmount() {
        val transaction = Transaction(
            id = "3",
            date = "2024-01-01T10:00:00Z",
            amount = -750.5,
            currency = "UAH",
            category = "Transport",
            description = "Taxi",
            type = "expense",
            bankAccountId = "acc123"
        )
        assertEquals(750.5, transaction.getAbsoluteAmount())
    }
}