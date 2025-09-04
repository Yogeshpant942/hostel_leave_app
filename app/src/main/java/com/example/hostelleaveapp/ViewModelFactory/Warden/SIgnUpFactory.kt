package com.example.hostelleaveapp.ViewModelFactory.Warden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary
import com.example.hostelleaveapp.LogInRepositary.wardenLogin.WardenLoginRepo
import com.example.hostelleaveapp.ViewModel.StudentViewModel.SignUpViewModel
import com.example.hostelleaveapp.ViewModel.WardenViewModel.signUpViewmodel

class SIgnUpFactory (private val repository: WardenLoginRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(signUpViewmodel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return signUpViewmodel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}