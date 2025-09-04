package com.example.hostelleaveapp.Ui.Warden

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hostelleaveapp.Adapter.RequestAdapter
import com.example.hostelleaveapp.DataModel.LeaveData
import com.example.hostelleaveapp.FCM.Retrofit
import com.example.hostelleaveapp.MainRepositary.WardenRepositary.WardenDataRepo
import com.example.hostelleaveapp.ViewModel.WardenViewModel.RequestViewModel
import com.example.hostelleaveapp.ViewModelFactory.Warden.RequestFactory
import com.example.hostelleaveapp.databinding.FragmentLeaveRequestBinding

class LeaveRequestFragment : Fragment() {

    private var _binding: FragmentLeaveRequestBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RequestViewModel
    private lateinit var requestAdapter: RequestAdapter
    private val allRequests = mutableListOf<LeaveData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaveRequestBinding.inflate(inflater, container, false)
        val repo = WardenDataRepo()
        val factory = RequestFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(RequestViewModel::class.java)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupChipGroup()
        observeRequests()
        viewModel.loadLeaveRequests()

        val filter = arguments?.getString("filter")
        when (filter?.lowercase()) {
            "pending" -> binding.chipPending.isChecked = true
            "approved", "accepted" -> binding.chipApproved.isChecked = true
            "rejected" -> binding.chipRejected.isChecked = true
            "all", null -> binding.chipAll.isChecked = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRecyclerView() {
        requestAdapter = RequestAdapter(
            requests = listOf(),
            isPendingScreen = false,
            onAcceptClick = { request ->
                request.studentId?.let { id ->
                    viewModel.updateDate(requireContext(), id, "Approved")
                    Toast.makeText(requireContext(), "Approved", Toast.LENGTH_SHORT).show()
                }
            },
            onRejectClick = { request ->
                request.studentId?.let { id ->
                    viewModel.updateDate(requireContext(), id, "Rejected")
                    Toast.makeText(requireContext(), "Rejected", Toast.LENGTH_SHORT).show()
                }
            },
            onItemClick = { request ->
                Toast.makeText(requireContext(), "Clicked: ${request.name}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerLeaveRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLeaveRequests.adapter = requestAdapter
    }

    private fun observeRequests() {
        viewModel.leaveRequests.observe(viewLifecycleOwner) { list ->
            allRequests.clear()
            allRequests.addAll(list)
            list.forEach {
                Log.d("LeaveDebug", "Request: ${it.name}, Status: ${it.leaveStatus}")
            }
            filterRequests(binding.chipGroup.checkedChipId)
        }
    }

    private fun setupChipGroup() {
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            filterRequests(checkedId)
        }
    }

    private fun filterRequests(checkedId: Int) {
        val filteredList = when (checkedId) {
            binding.chipPending.id -> allRequests.filter {
                it.leaveStatus?.trim()?.equals("Pending", ignoreCase = true) == true || it.leaveStatus == null
            }
            binding.chipApproved.id -> allRequests.filter {
                it.leaveStatus?.trim()?.equals("Approved", ignoreCase = true) == true
            }
            binding.chipRejected.id -> allRequests.filter {
                it.leaveStatus?.trim()?.equals("Rejected", ignoreCase = true) == true
            }
            else -> allRequests
        }
        val isPending = checkedId == binding.chipPending.id
        requestAdapter.updateData(filteredList, isPending)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
