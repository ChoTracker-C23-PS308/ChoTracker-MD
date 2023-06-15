package com.capstone.chotracker.ui.chochat.mycho_bot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.chotracker.databinding.RecyclerMessageSentBinding
import com.capstone.chotracker.databinding.RecyclerMessageReceivedBinding

class MychoBotAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages: MutableList<String> = mutableListOf()

    companion object {
        private const val VIEW_TYPE_SENT = 0
        private const val VIEW_TYPE_RECEIVED = 1
    }

    inner class SentMessageViewHolder(private val binding: RecyclerMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String) {
            binding.tvSentMessage.text = message
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: RecyclerMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String) {
            binding.tvReceivedMessage.text = message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val binding = RecyclerMessageSentBinding.inflate(inflater, parent, false)
                SentMessageViewHolder(binding)
            }
            VIEW_TYPE_RECEIVED -> {
                val binding = RecyclerMessageReceivedBinding.inflate(inflater, parent, false)
                ReceivedMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    fun addMessage(message: String) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}