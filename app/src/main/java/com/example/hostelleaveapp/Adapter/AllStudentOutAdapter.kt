package com.example.hostelleaveapp.Adapter

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hostelleaveapp.DataModdel.StudentLeaveDetail
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.databinding.AllStudentOutBinding
import com.example.hostelleaveapp.databinding.ItemOutStudentBinding
import android.view.ViewGroup as ViewGroup1

class AllStudentOutAdapter (private val onCallClick: (String) -> Unit
) : RecyclerView.Adapter<AllStudentOutAdapter.OutStudentViewHolder>() {

    private val studentList = mutableListOf<StudentLeaveDetail>()
    inner class OutStudentViewHolder(val binding: AllStudentOutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup1, viewType: Int): OutStudentViewHolder {
        val binding = AllStudentOutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OutStudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OutStudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.binding.apply {
            tvName.text = student.name
            tvRollNo.text = "Roll No: ${student.rollNo}"
            tvDates.text = "From: ${student.start_date} To: ${student.end_date}"
            tvReason.text = "Reason: ${student.leaveReason}"

            Glide.with(imgStudent.context)
                .load(student.profileImage)
                .placeholder(R.drawable.ic_profile)
                .into(imgStudent)

            btnCall.setOnClickListener {
                onCallClick(student.PhoneNo!!)
            }
        }
    }

    override fun getItemCount(): Int = studentList.size

    fun updateList(newList: List<StudentLeaveDetail>) {
        studentList.clear()
        studentList.addAll(newList)
        notifyDataSetChanged()
    }
}
