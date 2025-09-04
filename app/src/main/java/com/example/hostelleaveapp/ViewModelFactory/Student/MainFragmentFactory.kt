package com.example.hostelleaveapp.ViewModelFactory.Student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hostelleaveapp.MainRepositary.StudentMainRepo.StudentMainDataRepo
import com.example.hostelleaveapp.ViewModel.StudentViewModel.LeaveApplyViewModel

class MainFragmentFactory(private val repository: StudentMainDataRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaveApplyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeaveApplyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
