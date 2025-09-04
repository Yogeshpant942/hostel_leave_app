package com.example.hostelleaveapp.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hostelleaveapp.MainRepositary.GuardRepo.GuardMainDataRepo
import com.example.hostelleaveapp.ViewModel.GuardLeaveViewModel
import com.example.hostelleaveapp.ViewModel.WardenViewModel.signUpViewmodel

class StudentDetailFactory(private val repo: GuardMainDataRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GuardLeaveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GuardLeaveViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
