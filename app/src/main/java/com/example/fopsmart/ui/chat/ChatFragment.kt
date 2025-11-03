package com.example.fopsmart.ui.chat

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fopsmart.R
import com.example.fopsmart.adapter.ChatAdapter
import com.example.fopsmart.data.model.ChatMessage
import com.example.fopsmart.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()

    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var fr: FrameLayout

    companion object {
        fun newInstance() = ChatFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fr = binding.main

        val params = fr.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, 0, 220)
        fr.layoutParams = params

        setupRecyclerView()

        setupClickListeners()

        setupObservers()

        binding.root.doOnPreDraw {
            setupKeyboardListener()
            setupInputFocusListener()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList)
        binding.chatRecycler.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
        }
    }

    private fun setupClickListeners() {
        binding.sendButton.setOnClickListener {
            val query = binding.messageInput.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.sendMessage(query)
                binding.messageInput.text.clear()
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.messageInput.windowToken, 0)
        binding.messageInput.clearFocus()
    }

    private fun setupObservers() {
        // Спостерігач за списком повідомлень
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            val wasEmpty = messageList.isEmpty()
            messageList.clear()
            messageList.addAll(messages)
            chatAdapter.notifyDataSetChanged()

            if (messages.isNotEmpty()) {
                binding.chatRecycler.smoothScrollToPosition(messages.size - 1)

                if (wasEmpty) {
                    binding.emptyState.visibility = View.GONE
                    binding.chatRecycler.visibility = View.VISIBLE
                }
            } else {
                binding.emptyState.visibility = View.VISIBLE
                binding.chatRecycler.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.sendButton.isEnabled = !isLoading
        }
    }

    private fun setupInputFocusListener() {
        binding.messageInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.root.postDelayed({
                    ViewCompat.requestApplyInsets(binding.root)
                }, 50)
            }
        }
    }

    private fun setupKeyboardListener() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val keyboardHeight = imeInsets.bottom - systemBarsInsets.bottom
            val isKeyboardVisible = keyboardHeight > 0

            if (isKeyboardVisible) {
                val params = fr.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(0, 0, 0, 40)
                fr.layoutParams = params
                animateInputContainerUp(keyboardHeight)
            } else {
                val params = fr.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(0, 0, 0, 220)
                fr.layoutParams = params
                animateInputContainerDown()
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun animateInputContainerUp(keyboardHeight: Int) {
        binding.inputContainer.animate()
            .translationY(-keyboardHeight.toFloat())
            .setDuration(200)
            .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator())
            .start()
    }

    private fun animateInputContainerDown() {
        binding.inputContainer.animate()
            .translationY(0f)
            .setDuration(300)
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}