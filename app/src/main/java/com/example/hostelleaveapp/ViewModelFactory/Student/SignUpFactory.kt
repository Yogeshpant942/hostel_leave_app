package com.example.hostelleaveapp.ViewModelFactory.Student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary
import com.example.hostelleaveapp.ViewModel.StudentViewModel.SignUpViewModel

class SignUpFactory (private val repository: StudentRepositary) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}