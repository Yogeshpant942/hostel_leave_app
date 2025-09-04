package com.example.hostelleaveapp.Adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hostelleaveapp.DataModel.LeaveData
import com.example.hostelleaveapp.DataModel.StudentData
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.databinding.LeaveHistoryItemBinding

class LeaveHistoryAdapter: RecyclerView.Adapter<LeaveHistoryAdapter.LeaveViewHolder>() {
    private var studentList: MutableList<LeaveData> = mutableListOf()
    fun updateData(newList: List<LeaveData>) {
        studentList.clear()
        studentList.addAll(newList)
        notifyDataSetChanged()
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
      val binding = LeaveHistoryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  LeaveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = studentList.size

   inner class LeaveViewHolder(private val binding: LeaveHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
            val data = studentList[position]
            tvStartDate.text = data.start_date
            tvEndDate.text = data.end_date
            tvReason.text = data.leaveReason
                var status = data.leaveStatus
                when (status) {
                    "Approved" -> {
                        binding.chipStatus.text = "Approved"
                        binding.chipStatus.setChipBackgroundColorResource(R.color.green)
                        binding.chipStatus.setTextColor(Color.WHITE)
                    }
                    "Rejected" -> {
                        binding.chipStatus.text = "Rejected"
                        binding.chipStatus.setChipBackgroundColorResource(R.color.red)
                        binding.chipStatus.setTextColor(Color.WHITE)
                    }
                    else -> {
                        binding.chipStatus.text = "Pending"
                        binding.chipStatus.setChipBackgroundColorResource(R.color.yellow)
                        binding.chipStatus.setTextColor(Color.BLACK)
                    }
                }

            }
        }
    }
}