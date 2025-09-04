package com.example.hostelleaveapp.ViewModelFactory.Student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hostelleaveapp.MainRepositary.StudentMainRepo.StudentMainDataRepo
import com.example.hostelleaveapp.ViewModel.StudentViewModel.LeaveApplyViewModel
import com.example.hostelleaveapp.ViewModel.StudentViewModel.LeaveHistoryViewModel

class LeaveHistoryFactory (private val repository: StudentMainDataRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaveHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeaveHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
