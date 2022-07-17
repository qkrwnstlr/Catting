package com.example.catting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.catting.databinding.ActivityChattingBinding

class ChattingActivity : AppCompatActivity() {
    val binding by lazy { ActivityChattingBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}