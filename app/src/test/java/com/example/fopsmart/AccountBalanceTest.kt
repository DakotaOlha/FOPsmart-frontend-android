package com.example.fopsmart

import com.example.fopsmart.data.model.AccountBalance
import org.junit.Test
import kotlin.test.assertEquals

class AccountBalanceTest {
    @Test
    fun testAccountBalance_Creation() {
        val balance = AccountBalance(
            id = 1,
            balance = "5000.50",
            currencyCode = 980,
            iban = "UA123456789",
            type = "CHECKING"
        )
        assertEquals(1, balance.id)
        assertEquals("5000.50", balance.balance)
        assertEquals("UA123456789", balance.iban)
    }

    @Test
    fun testMultipleAccounts() {
        val accounts = listOf(
            AccountBalance(1, "1000", 980, "IBAN1", "CHECKING"),
            AccountBalance(2, "2000", 980, "IBAN2", "SAVINGS")
        )
        assertEquals(2, accounts.size)
        assertEquals("1000", accounts[0].balance)
    }
}