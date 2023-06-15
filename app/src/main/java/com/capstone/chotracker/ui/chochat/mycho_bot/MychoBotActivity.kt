package com.capstone.chotracker.ui.chochat.mycho_bot

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.chotracker.custom_view.CustomPopUpAlert
import com.capstone.chotracker.data.response.chobot.ChobotResponseModel
import com.capstone.chotracker.databinding.ActivityMychoBotBinding
import com.capstone.chotracker.utils.ResultCondition


class MychoBotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMychoBotBinding
    private lateinit var viewModel: MychoBotViewModel
    private lateinit var adapter: MychoBotAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMychoBotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MychoBotViewModel::class.java)
        setupRecyclerView(this)

        viewModel.messageResult.observe(this) { resultCondition ->
            when (resultCondition) {
                is ResultCondition.SuccessState -> {
                    val message = resultCondition.data
                    adapter.addMessage(message)
                    binding.rvMychobot.scrollToPosition(adapter.itemCount - 1)
                }
                is ResultCondition.ErrorState -> {
                    showError(resultCondition.data, true)
                }
                is ResultCondition.LoadingState -> {  }
            }
        }

        binding.buttonSend.setOnClickListener {
            val message = binding.etChochatTyping.text.toString()
            if (message.isNotBlank()) {
                viewModel.sendMessage(message)
                binding.etChochatTyping.text.clear()
                adapter.addMessage(message)
                binding.rvMychobot.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun showError(errorMessage: Int, error: Boolean) {
        if (error) {
            val alert = CustomPopUpAlert(this, errorMessage)
            alert.show()
            alert.setOnDismissListener {
                finish()
            }
        }
    }

    private fun setupRecyclerView(context: Context) {
        val rvMychoBot = binding.rvMychobot

        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvMychoBot.layoutManager = GridLayoutManager(context, 2)
        } else {
            rvMychoBot.layoutManager = LinearLayoutManager(context)
        }

        adapter = MychoBotAdapter()
        rvMychoBot.adapter = adapter
    }
}