package com.example.hostelleaveapp.Ui.Student
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.MainRepositary.StudentMainRepo.StudentMainDataRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.StudentViewModel.LeaveApplyViewModel
import com.example.hostelleaveapp.ViewModelFactory.Student.MainFragmentFactory
import com.example.hostelleaveapp.databinding.FragmentStudentMainBinding

class StudentMainFragment : Fragment() {
    private lateinit var binding: FragmentStudentMainBinding
    private lateinit var viewModel: LeaveApplyViewModel
    private val TAG = "StudentMainFragment"
    private var latestLeaveCount: Int = 0
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Started")

        val repo = StudentMainDataRepo()
        val factory = MainFragmentFactory(repo)
        viewModel = ViewModelProvider(this, factory)[LeaveApplyViewModel::class.java]
        binding = FragmentStudentMainBinding.inflate(inflater, container, false)
        viewModel.count_leave.observe(viewLifecycleOwner) { count ->
            latestLeaveCount = count
            binding.tvLeaveCount.text = "$count out of 4"
            Log.d("count",count.toString())
        }

        binding.btnApplyLeave.setOnClickListener {
            Log.d(TAG, "btnApplyLeave clicked: Navigating to LeaveApplyFragment")
            val bundle = Bundle().apply {
                putInt("leave_count", latestLeaveCount)
            }
            findNavController().navigate(R.id.action_studentMainFragment_to_leaveApplyFragment, bundle)
        }
          viewModel.fetchLeaveCount()

        binding.btnLeaveHistory.setOnClickListener {
            findNavController().navigate(R.id.action_studentMainFragment_to_leaveHistoryFragment)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logOutUser()
            findNavController().navigate(R.id.action_studentMainFragment_to_roleSelectFragment)
        }

        binding.btnContactAdmin.setOnClickListener {
            findNavController().navigate(R.id.action_studentMainFragment_to_chatHomeFragment)
        }
        viewModel.logoutStatus.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Logout failed", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.getStudentDetail()

        viewModel.Student_detail.observe(viewLifecycleOwner, Observer { student ->
            if (student != null) {
                binding.tvRollNo.text = student.rollNo
                binding.tvName.text = student.name

                student.profileImage?.let { base64String ->
                    try {
                        val cleanedBase64 = if (base64String.contains(",")) {
                            base64String.substringAfter(",")
                        } else base64String

                        val decodedBytes = Base64.decode(cleanedBase64, Base64.DEFAULT)
                        if (decodedBytes.isNotEmpty()) {
                            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            bitmap?.let {
                                binding.ivProfile.setImageBitmap(it)
                                Log.d(TAG, "Profile image loaded from base64.")
                            } ?: Log.e(TAG, "Bitmap decoding failed: null bitmap.")
                        } else {
                            Log.e(TAG, "Decoded bytes are empty.")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Base64 decode error: ${e.localizedMessage}")
                    }
                }
            } else {
                Log.d(TAG, "Student details are null")
            }
        })


        return binding.root
    }
}
