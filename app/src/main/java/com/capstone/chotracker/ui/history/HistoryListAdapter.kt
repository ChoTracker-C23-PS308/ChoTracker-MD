package com.capstone.chotracker.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.chotracker.R
import com.capstone.chotracker.data.response.history.HistoryListResponseModel
import com.capstone.chotracker.databinding.RecyclerItemHistoryBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HistoryListAdapter : ListAdapter<HistoryListResponseModel, HistoryListAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerItemHistoryBinding.inflate(inflater, parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
    }

    inner class HistoryViewHolder(private val binding: RecyclerItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: HistoryListResponseModel) {
            with(binding) {
                tvHistoryDate.text = formatDate(history.date)
                tvTotalCholesterol.text = history.totalCholesterol.toString()
                val tingkat = history.tingkat

                if (tingkat == "Normal") {
                    tvCholesterolLevel.text = history.tingkat
                    tvCholesterolLevel.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                } else if (tingkat == "At Risk") {
                    tvCholesterolLevel.text = history.tingkat
                    tvCholesterolLevel.setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                } else {
                    tvCholesterolLevel.text = history.tingkat
                    tvCholesterolLevel.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                }

            }
        }

        private fun formatDate(date: String?): String? {
            if (date != null) {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                try {
                    val parsedDate = inputFormat.parse(date)
                    return outputFormat.format(parsedDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            return "1 Agu 1945"
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryListResponseModel>() {
            override fun areItemsTheSame(
                oldItem: HistoryListResponseModel,
                newItem: HistoryListResponseModel
            ): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(
                oldItem: HistoryListResponseModel,
                newItem: HistoryListResponseModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
