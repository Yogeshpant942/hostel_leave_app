package com.example.hostelleaveapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hostelleaveapp.DataModel.LeaveData
import com.example.hostelleaveapp.databinding.AllPendingRequestBinding
import com.example.hostelleaveapp.databinding.ItemRequestReadOnlyBinding

class RequestAdapter(
    private var requests: List<LeaveData>,
    private var isPendingScreen: Boolean,
    private val onAcceptClick: (LeaveData) -> Unit,
    private val onRejectClick: (LeaveData) -> Unit,
    private val onItemClick: (LeaveData) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_PENDING = 0
        private const val TYPE_STATUS = 1
    }

    inner class PendingViewHolder(private val binding: AllPendingRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(request: LeaveData) {
            binding.tvStudentName.text = request.name
            binding.tvReason.text = request.leaveReason
            binding.tvStartDate.text = request.start_date
            binding.tvEndDate.text = request.end_date
            binding.btnAccept.setOnClickListener { onAcceptClick(request) }
            binding.btnReject.setOnClickListener { onRejectClick(request) }
            binding.root.setOnClickListener { onItemClick(request) }
        }
    }

    inner class StatusViewHolder(private val binding: ItemRequestReadOnlyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(request: LeaveData) {
            binding.tvStudentName.text = request.name
            binding.tvReason.text = request.leaveReason
            binding.tvStartDate.text = request.start_date
            binding.tvEndDate.text = request.end_date
            binding.tvStatus.text = request.leaveStatus
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPendingScreen) TYPE_PENDING else TYPE_STATUS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_PENDING) {
            PendingViewHolder(
                AllPendingRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            StatusViewHolder(
                ItemRequestReadOnlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val request = requests[position]
        if (holder is PendingViewHolder) holder.bind(request)
        else if (holder is StatusViewHolder) holder.bind(request)
    }

    override fun getItemCount() = requests.size

    fun updateData(newList: List<LeaveData>, isPending: Boolean) {
        this.requests = newList
        this.isPendingScreen = isPending
        notifyDataSetChanged()
    }
}
