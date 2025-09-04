package com.example.hostelleaveapp.Ui.Guard

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hostelleaveapp.Adapter.AllStudentOutAdapter
import com.example.hostelleaveapp.MainRepositary.GuardRepo.GuardMainDataRepo
import android.Manifest
import android.app.Activity
import com.example.hostelleaveapp.ViewModel.GuardLeaveViewModel
import com.example.hostelleaveapp.ViewModelFactory.StudentDetailFactory
import com.example.hostelleaveapp.databinding.FragmentAllOutStudentBinding


class AllOutStudentFragment : Fragment() {

lateinit var binding:FragmentAllOutStudentBinding
lateinit var viewModel :GuardLeaveViewModel
lateinit var adapter:AllStudentOutAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllOutStudentBinding.inflate(layoutInflater,container,false)
        val repo = GuardMainDataRepo()
        val factory = StudentDetailFactory(repo)
        viewModel = ViewModelProvider(this,factory)[GuardLeaveViewModel::class.java]

        viewModel.fetch_leave_Status_guard()

        adapter = AllStudentOutAdapter(onCallClick = {phoneNo->

            makeAcall(phoneNo)

        })
        binding.recyclerOutStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOutStudents.adapter = adapter

        viewModel.guardLeaveStatus.observe(viewLifecycleOwner){
            adapter.updateList(it)
        }


        return binding.root
    }

    private fun makeAcall(phoneNo: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNo")
        }

        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            startActivity(intent)
        }
        else{
            ActivityCompat.requestPermissions(
                requireContext() as Activity,  arrayOf(Manifest.permission.CALL_PHONE),
                1)
        }
    }


}