package com.example.fopsmart

import CarouselAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.fopsmart.ui.login.LoginActivity

class DescActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorLayout: LinearLayout
    private lateinit var textViewDesc: TextView
    private lateinit var buttonContinue: Button
    private val indicators = mutableListOf<ImageView>()

    private val images = listOf(
        R.drawable.img_analitics,
        R.drawable.img_limits,
        R.drawable.img_ai
    )

    private val descriptions = listOf(
        "Веди автоматичний облік доходів та витрат",
        "Контролюй ліміти доходу та отримуй нагадування",
        "Запитуй у АІ-помічника та генеруй звіти"
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desc)

        viewPager = findViewById(R.id.viewPager)
        indicatorLayout = findViewById(R.id.indicatorLayout)
        textViewDesc = findViewById(R.id.textViewDesc)
        buttonContinue = findViewById(R.id.button)

        val adapter = CarouselAdapter(images)
        viewPager.adapter = adapter

        setupIndicators()
        setCurrentIndicator(0)
        updateDescription(0)

        textViewDesc.setOnTouchListener { v, event ->
            viewPager.dispatchTouchEvent(event)
            true
        }

        indicatorLayout.setOnTouchListener { v, event ->
            viewPager.dispatchTouchEvent(event)
            true
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                updateDescription(position)
            }
        })

        buttonContinue.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish();
        }
    }

    private fun setupIndicators() {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(50, 0, 50, 0)
        }

        for (i in images.indices) {
            val indicator = ImageView(this).apply {
                setImageDrawable(ContextCompat.getDrawable(
                    this@DescActivity,
                    R.drawable.indicator_inactive
                ))
                layoutParams = params
            }
            indicatorLayout.addView(indicator)
            indicators.add(indicator)
        }
    }

    private fun setCurrentIndicator(position: Int) {
        for (i in indicators.indices) {
            val drawableId = if (i == position) {
                R.drawable.indicator_active
            } else {
                R.drawable.indicator_inactive
            }
            indicators[i].setImageDrawable(
                ContextCompat.getDrawable(this, drawableId)
            )
        }
    }

    private fun updateDescription(position: Int) {
        if (position < descriptions.size) {
            textViewDesc.text = descriptions[position]
        }
    }
}