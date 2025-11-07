package com.example.fopsmart

import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.fopsmart.MainActivity
import com.example.fopsmart.R
import com.example.fopsmart.adapter.TransactionAdapter
import com.example.fopsmart.data.model.Transaction
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionAdapterTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var adapter: TransactionAdapter
    private val mockTransactions = listOf(
        Transaction(
            id = "1",
            date = "2024-01-01T10:00:00Z",
            amount = -500.0,
            currency = "UAH",
            category = "Food",
            description = "Grocery shopping",
            type = "expense",
            bankAccountId = "acc123"
        ),
        Transaction(
            id = "2",
            date = "2024-01-02T10:00:00Z",
            amount = 5000.0,
            currency = "UAH",
            category = "Salary",
            description = "Monthly salary",
            type = "income",
            bankAccountId = "acc123"
        )
    )

    @Before
    fun setup() {
        adapter = TransactionAdapter { }
    }

    @Test
    fun testTransactionAdapter_ItemCount() {
        adapter.submitList(mockTransactions)
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun testTransactionAdapter_MultipleItems() {
        val manyTransactions = mockTransactions + mockTransactions
        adapter.submitList(manyTransactions)
        assertEquals(4, adapter.itemCount)
    }

    @Test
    fun testTransactionAdapter_EmptyList() {
        adapter.submitList(emptyList())
        assertEquals(0, adapter.itemCount)
    }
}