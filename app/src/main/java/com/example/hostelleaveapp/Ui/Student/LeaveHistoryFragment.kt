package com.example.hostelleaveapp.Ui.Student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hostelleaveapp.Adapter.LeaveHistoryAdapter
import com.example.hostelleaveapp.DataModel.LeaveData
import com.example.hostelleaveapp.MainRepositary.StudentMainRepo.StudentMainDataRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.StudentViewModel.LeaveHistoryViewModel
import com.example.hostelleaveapp.ViewModelFactory.Student.LeaveHistoryFactory
import com.example.hostelleaveapp.databinding.FragmentLeaveHistoryBinding

class LeaveHistoryFragment : Fragment() {
      lateinit var binding:FragmentLeaveHistoryBinding
      lateinit var viewModel:LeaveHistoryViewModel
      lateinit var adapter:LeaveHistoryAdapter
       private val list = ArrayList<LeaveData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaveHistoryBinding.inflate(layoutInflater,container,false)
        val repo = StudentMainDataRepo()
        val factory = LeaveHistoryFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(LeaveHistoryViewModel::class.java)

        adapter = LeaveHistoryAdapter()
        binding.recyclerLeaveHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLeaveHistory.adapter = adapter
      viewModel.fetchLeaveHistory()
        viewModel.leave_data.observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })

        return binding.root
    }


}