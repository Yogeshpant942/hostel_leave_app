package com.example.hostelleaveapp.ViewModelFactory.Warden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary
import com.example.hostelleaveapp.MainRepositary.WardenRepositary.WardenDataRepo
import com.example.hostelleaveapp.ViewModel.StudentViewModel.SignUpViewModel
import com.example.hostelleaveapp.ViewModel.WardenViewModel.RequestViewModel

class RequestFactory (private val repository: WardenDataRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RequestViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}