package com.example.fopsmart.ui.chat

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import com.example.fopsmart.R
import com.example.fopsmart.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!


    companion object {
        fun newInstance() = ChatFragment()
    }

    private val viewModel: ChatViewModel by viewModels()

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

        binding.root.doOnPreDraw {
            setupKeyboardListener()
            setupInputFocusListener()
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
                animateInputContainerUp(keyboardHeight)
            } else {
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
}