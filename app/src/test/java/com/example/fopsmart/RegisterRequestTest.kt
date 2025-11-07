package com.example.fopsmart

import com.example.fopsmart.data.model.RegisterRequest
import org.junit.Test
import kotlin.test.assertEquals

class RegisterRequestTest {
    @Test
    fun testRegisterRequest_AllFields() {
        val request = RegisterRequest(
            email = "user@test.com",
            password = "pass123",
            firstName = "John",
            lastName = "Doe",
            fopGroup = 1
        )
        assertEquals("user@test.com", request.email)
        assertEquals("pass123", request.password)
        assertEquals("John", request.firstName)
        assertEquals("Doe", request.lastName)
    }

    @Test
    fun testRegisterRequest_DefaultGroup() {
        val request = RegisterRequest(
            email = "user@test.com",
            password = "pass123",
            firstName = "Jane",
            lastName = "Smith"
        )
        assertEquals(1, request.fopGroup)
    }
}
