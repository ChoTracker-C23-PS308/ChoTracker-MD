package com.capstone.chotracker.ui.findkes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.capstone.chotracker.data.response.findkes.FindkesModel
import com.capstone.chotracker.databinding.RecyclerItemFindkesBinding
import androidx.recyclerview.widget.ListAdapter
import com.capstone.chotracker.data.response.history.HistoryListResponseModel
import com.capstone.chotracker.ui.history.HistoryListAdapter

class FindkesAdapter(private val context: Context) :
    ListAdapter<FindkesModel, FindkesAdapter.ViewHolder>(DIFF_CALLBACK) {

    interface OnItemClickListener {
        fun onItemClick(hospital: FindkesModel)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RecyclerItemFindkesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hospital = getItem(position)
        holder.bind(hospital)
    }

    inner class ViewHolder(private val binding: RecyclerItemFindkesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val hospital = getItem(position)
                    itemClickListener?.onItemClick(hospital)
                }
            }
        }

        fun bind(hospital: FindkesModel) {
            binding.tvHospitalName.text = hospital.name
            binding.tvHospitalLocation.text = hospital.vicinity
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FindkesModel>() {
            override fun areItemsTheSame(
                oldItem: FindkesModel,
                newItem: FindkesModel
            ): Boolean {
                return oldItem.placeId == newItem.placeId
            }

            override fun areContentsTheSame(
                oldItem: FindkesModel,
                newItem: FindkesModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
