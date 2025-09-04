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
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.MainRepositary.GuardRepo.GuardMainDataRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.GuardLeaveViewModel
import com.example.hostelleaveapp.ViewModelFactory.StudentDetailFactory
import com.example.hostelleaveapp.databinding.FragmentStudentDetailBinding

class StudentDetailFragment : Fragment() {

    private lateinit var binding: FragmentStudentDetailBinding
    private lateinit var viewModel: GuardLeaveViewModel
    private var encodedImage: String = ""
    private val TAG = "StudentDetailFragment"

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentDetailBinding.inflate(inflater, container, false)
        val repo = GuardMainDataRepo()
        val factory = StudentDetailFactory(repo)
        viewModel = ViewModelProvider(this, factory)[GuardLeaveViewModel::class.java]

        val rollNo = requireArguments().getString("rollNo") ?: ""
        viewModel.fetch_Stdent_leave_Status(rollNo)

        viewModel.student_leaveStatus.observe(viewLifecycleOwner) { studentData ->
            binding.tvName.text = "Name: ${studentData.studentDetails?.name.orEmpty()}"
            binding.tvRoll.text = "Roll No: ${studentData.studentDetails?.rollNo.orEmpty()}"
            binding.tvPhone.text = "Phone: ${studentData.studentDetails?.PhoneNo.orEmpty()}"
            binding.tvStartDate.text = "Start Date: ${studentData.leaveDetails?.start_date.orEmpty()}"
            binding.tvEndDate.text = "End Date: ${studentData.leaveDetails?.end_date.orEmpty()}"
            binding.tvReason.text = "Reason: ${studentData.leaveDetails?.leaveReason.orEmpty()}"
            binding.tvDestination.text = "Destination: ${studentData.leaveDetails?.destination.orEmpty()}"
            binding.tvStatus.text = "Status: ${studentData.leaveDetails?.leaveStatus.orEmpty()}"

            studentData.studentDetails?.profileImage?.let { base64String ->
                try {
                    val cleanedBase64 = base64String.substringAfter(",")

                    val decodedBytes = Base64.decode(cleanedBase64, Base64.NO_WRAP)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                    if (bitmap != null) {
                        binding.imgProfile.setImageBitmap(bitmap)
                        encodedImage = cleanedBase64
                        Log.d(TAG, "Image loaded successfully")
                    } else {
                        Log.e(TAG, "Bitmap is null after decoding")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Image decoding error: ${e.message}")
                }
            } ?: run {
                Log.e(TAG, "Base64 image string is null")
            }

            val name = studentData.studentDetails?.name.orEmpty()
            val roll = studentData.studentDetails?.rollNo.orEmpty()
            val phone = studentData.studentDetails?.PhoneNo.orEmpty()
            val reason = studentData.leaveDetails?.leaveReason.orEmpty()
            val startDate = studentData.leaveDetails?.start_date.orEmpty()
            val endDate = studentData.leaveDetails?.end_date.orEmpty()
            val navigted_bool = arguments?.getBoolean("navigated") ?: false

            if(navigted_bool == false){
                val confidenceString = requireArguments().getString("match_confidence")
                val confidence = confidenceString?.toDoubleOrNull() ?: 0.00
                if(confidence>=80){
                    MatchedFragment().show(childFragmentManager,"matchedDialog")
                }
                else{
                    NotMatchedFragment().show(childFragmentManager,"NotMatchedDialog")
                }
            }

            binding.btnAccept.setOnClickListener {
                viewModel.upload_leaveStatus(
                    name, roll, phone,
                    "Approved", reason, startDate, endDate
                )
            }

            binding.btnReject.setOnClickListener {
                viewModel.upload_leaveStatus(
                    name, roll, phone,
                    "Rejected", reason, startDate, endDate
                )
            }
        }
        binding.btnOpenCamera.setOnClickListener {
            if (encodedImage.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putString("rollNo",rollNo)
                    putString("image_base64", encodedImage)
                }
                findNavController().navigate(
                    R.id.action_studentDetailFragment_to_faceCameraFragment,
                    bundle
                )
            } else {
                Toast.makeText(requireContext(), "Student image not loaded yet!", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
}
