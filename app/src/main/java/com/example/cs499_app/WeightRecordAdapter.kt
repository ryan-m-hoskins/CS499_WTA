package com.example.cs499_app

import WeightRecord
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cs499_app.databinding.RecyclerViewRowBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeightRecordAdapter(
    private val onItemClick: (WeightRecord) -> Unit,
    // private val onMenuClick: (WeightRecord) -> Unit
) : RecyclerView.Adapter<WeightRecordAdapter.WeightViewHolder>() {
    private var weightRecords = listOf<WeightRecord>()

    fun updateRecords(newRecords: List<WeightRecord>) {
        weightRecords = newRecords.sortedByDescending { it.date }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val binding = RecyclerViewRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        holder.bind(weightRecords[position])
    }

    override fun getItemCount(): Int = weightRecords.size

    inner class WeightViewHolder(private val binding: RecyclerViewRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(record: WeightRecord) {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val date = Date(record.date)

            binding.weight.text = record.weight.toString()
            binding.date.text= dateFormat.format(date)

            binding.weightRecord.setOnClickListener {
                onItemClick(record)
            }
            /* TODO: Setup menu icon listener?
            binding.menuIcon.setOnClickListener {
                onMenuClick(record)
            }
             */
        }
    }
}