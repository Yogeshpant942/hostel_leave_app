package com.example.hostelleaveapp.Ui.Student

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.MainRepositary.StudentMainRepo.StudentMainDataRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.StudentViewModel.LeaveApplyViewModel
import com.example.hostelleaveapp.ViewModelFactory.Student.MainFragmentFactory
import com.example.hostelleaveapp.databinding.FragmentLeaveApplyBinding
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.Locale

class LeaveApplyFragment : Fragment() {
     lateinit var binding:FragmentLeaveApplyBinding
     private val calendar = Calendar.getInstance()
    lateinit var viewModel: LeaveApplyViewModel
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentLeaveApplyBinding.inflate(layoutInflater,container,false)
        val leave_count = arguments?.getInt("leave_count")
        val repo = StudentMainDataRepo()
        val factory = MainFragmentFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(LeaveApplyViewModel::class.java)
        binding.etFromDate.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener { showDatePickerDialog(this) }
        }
        binding.etToDate.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener { showDatePickerDialog(this) } }

        binding.btnSubmitLeave.setOnClickListener{
            if (leave_count != null) {
                if(leave_count>=4){
                    Toast.makeText(requireContext(), "Sorry you cannot apply more than 4 leaves in a month.", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_leaveApplyFragment_to_studentMainFragment)
                    return@setOnClickListener
                }
            }
                    var from_date = binding.etFromDate.text.toString()
                    var to_date = binding.etToDate.text.toString()
                    var destination = binding.etDestination.text.toString()
                    var reason  = binding.etReason.text.toString()
                    if(from_date!!.isNotEmpty() && to_date!!.isNotEmpty() && reason.isNotEmpty() && destination.isNotEmpty()){
                        viewModel.leaveDataApply(from_date,to_date,reason,"pending",destination)
                    }

        }
        viewModel.leaveApplyStatus.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess == true ) {
                    Toast.makeText(requireContext(), "Leave applied successfully", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_leaveApplyFragment_to_studentMainFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to apply leave",
                        Toast.LENGTH_SHORT

                    ).show()

            }
        }
    return binding.root
    }
private fun showDatePickerDialog(editText: EditText) {
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(requireContext(),{_,y,m,d->
        calendar.set(y,m,d)
        editText.setText(dateFormat.format(calendar.time))
    },year,month,day)
    datePickerDialog.show()
}
}