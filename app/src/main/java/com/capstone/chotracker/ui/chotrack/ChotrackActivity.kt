package com.capstone.chotracker.ui.chotrack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.chotracker.databinding.ActivityChotrackBinding

class ChotrackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChotrackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChotrackBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}