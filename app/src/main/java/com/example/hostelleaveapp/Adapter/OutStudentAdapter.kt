package com.example.hostelleaveapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hostelleaveapp.DataModdel.StudentLeaveDetail
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.databinding.ItemOutStudentBinding

class OutStudentsAdapter(
    private var students: List<StudentLeaveDetail>
) : RecyclerView.Adapter<OutStudentsAdapter.OutStudentViewHolder>() {

    inner class OutStudentViewHolder(val binding: ItemOutStudentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutStudentViewHolder {
        val binding = ItemOutStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OutStudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OutStudentViewHolder, position: Int) {
        val student = students[position]
        with(holder.binding) {
            tvStudentName.text = student.name
            tvStudentRoll.text = student.rollNo
            tvLeaveTime.text = "Out since: ${student.time}"

            if (!student.profileImage.isNullOrEmpty()) {
                Glide.with(root.context)
                    .load(student.profileImage)
                    .placeholder(R.drawable.ic_profile)
                    .into(imgStudent)
            } else {
                imgStudent.setImageResource(R.drawable.ic_profile)
            }
        }
    }

    override fun getItemCount(): Int = students.size

    fun updateList(newList: List<StudentLeaveDetail>) {
        students = newList
        notifyDataSetChanged()
    }

}
