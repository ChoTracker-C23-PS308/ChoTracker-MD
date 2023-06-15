package com.capstone.chotracker.ui.chochat.choose_doctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.chotracker.databinding.ActivityChooseDoctorBinding

class ChooseDoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseDoctorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}