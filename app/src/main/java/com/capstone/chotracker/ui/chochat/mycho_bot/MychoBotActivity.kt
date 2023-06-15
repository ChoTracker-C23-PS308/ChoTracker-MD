package com.capstone.chotracker.ui.chochat.mycho_bot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.chotracker.databinding.ActivityMychoBotBinding

class MychoBotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMychoBotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMychoBotBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}