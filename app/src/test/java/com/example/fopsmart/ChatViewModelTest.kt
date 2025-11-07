package com.example.fopsmart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.fopsmart.data.model.ChatMessage
import com.example.fopsmart.ui.chat.ChatViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        viewModel = ChatViewModel()
    }

    @Test
    fun testInitialState() {
        val messages = viewModel.messages.value
        assertTrue(messages?.isEmpty() == true)
        assertTrue(viewModel.isLoading.value == false)
    }

    @Test
    fun testChatMessage_UserMessage() {
        val message = ChatMessage("Hello", isFromUser = true)
        assertTrue(message.isFromUser)
        assertEquals("Hello", message.text)
    }

    @Test
    fun testChatMessage_BotMessage() {
        val message = ChatMessage("Hi there!", isFromUser = false)
        assertTrue(!message.isFromUser)
    }
}
