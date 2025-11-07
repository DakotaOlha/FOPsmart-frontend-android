package com.example.fopsmart

import com.example.fopsmart.data.model.ConnectionInfo
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConnectionInfoTest {
    @Test
    fun testConnectionInfo_WithAllData() {
        val info = ConnectionInfo(
            clientName = "Monobank",
            clientId = "client123",
            lastSync = "2024-01-15T10:30:00Z",
            connectedAt = "2024-01-01T08:00:00Z"
        )
        assertEquals("Monobank", info.clientName)
        assertEquals("client123", info.clientId)
    }

    @Test
    fun testConnectionInfo_PartialData() {
        val info = ConnectionInfo(
            clientName = "TestClient",
            clientId = "id456"
        )
        assertEquals("TestClient", info.clientName)
        assertNull(info.lastSync)
    }
}