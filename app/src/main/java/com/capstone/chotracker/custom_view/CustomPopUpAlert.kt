package com.capstone.chotracker.custom_view

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.capstone.chotracker.R

class CustomPopUpAlert(context: Context, private val message: Int): AlertDialog(context) {
    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_pop_up_alert)

        val messageView = findViewById<TextView>(R.id.tv_error)
        messageView.text = context.getString(message)

        val close = findViewById<TextView>(R.id.tv_close)
        close.setOnClickListener {
            dismiss()
        }
    }
}