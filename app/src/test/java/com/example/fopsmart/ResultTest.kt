package com.example.fopsmart

import com.example.fopsmart.data.Result
import com.example.fopsmart.data.model.LoggedInUser
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResultTest {
    @Test
    fun testResult_Success() {
        val user = LoggedInUser("123", "John Doe")
        val result = Result.Success(user)
        assertTrue(result is Result.Success)
        assertEquals("John Doe", (result as Result.Success).data.displayName)
    }

    @Test
    fun testResult_Error() {
        val exception = IOException("Network error")
        val result = Result.Error(exception)
        assertTrue(result is Result.Error)
        assertEquals("Network error", (result as Result.Error).exception.message)
    }
}