package com.example.hostelleaveapp.Ui.Guard

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hostelleaveapp.Adapter.OutStudentsAdapter
import com.example.hostelleaveapp.DataModdel.StudentLeaveDetail
import com.example.hostelleaveapp.MainRepositary.GuardRepo.GuardMainDataRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.GuardLeaveViewModel
import com.example.hostelleaveapp.ViewModelFactory.StudentDetailFactory
import com.example.hostelleaveapp.databinding.FragmentGuardMainScreenBinding

class GuardMainScreenFragment : Fragment() {
    private lateinit var binding: FragmentGuardMainScreenBinding
    private lateinit var viewModel: GuardLeaveViewModel
    private lateinit var adapter: OutStudentsAdapter
    private lateinit var students: List<StudentLeaveDetail>
    private val TAG = "GuardMainFragment"

    @SuppressLint("SetTextI18n")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        students = ArrayList()
        binding = FragmentGuardMainScreenBinding.inflate(inflater, container, false)
        val repo = GuardMainDataRepo()
        val factory = StudentDetailFactory(repo)
        viewModel = ViewModelProvider(this, factory)[GuardLeaveViewModel::class.java]
        adapter = OutStudentsAdapter(students)
        binding.recyclerOutStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOutStudents.adapter = adapter
        viewModel.guardLeaveStatus.observe(viewLifecycleOwner) {
                studentList ->
            if (!studentList.isNullOrEmpty()) {
                adapter.updateList(studentList)
                val totalStudent = studentList.size
                binding.tvOutCount.text = "$totalStudent Students"
                binding.recyclerOutStudents.visibility = View.VISIBLE
            } else {
                adapter.updateList(emptyList())
                binding.tvOutCount.text = "Enjoy,0 Students"
                binding.recyclerOutStudents.visibility = View.GONE
            }
        }
        viewModel.fetch_leave_Status_guard()
        binding.btnScanId.setOnClickListener {
            val bottomSheet = GuardBarCodeOptions{optionSelected->
                when(optionSelected){
                    "Out"->findNavController().navigate(R.id.action_guardMainScreenFragment_to_barCodeFragment)

                    "In"->findNavController().navigate(R.id.action_guardMainScreenFragment_to_studentBarCodeInFragment)
                }
            }
            bottomSheet.show(parentFragmentManager, "CheckInOrOut")
        }
        binding.tvViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_guardMainScreenFragment_to_allOutStudentFragment)
        }
        viewModel.guardData.observe(viewLifecycleOwner){guardData->
            binding.tvGuardName.text = guardData!!.name
            binding.tvGuardId.text = guardData.guardId
            guardData.profileImage?.let { base64String ->
                try {
                    val cleanedBase64 = if (base64String.contains(",")) {
                        base64String.substringAfter(",")
                    } else base64String

                    val decodedBytes = Base64.decode(cleanedBase64, Base64.DEFAULT)
                    if (decodedBytes.isNotEmpty()) {
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        bitmap?.let {
                            binding.imgGuardProfile.setImageBitmap(it)
                            Log.d(TAG, "Profile image loaded from base64.")
                        } ?: Log.e(TAG, "Bitmap decoding failed: null bitmap.")
                    } else {
                        Log.e(TAG, "Decoded bytes are empty.")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Base64 decode error: ${e.localizedMessage}")
                }
            }
        }
        viewModel.fetchGuardDetail()
        return binding.root
    }
}
