package com.example.hostelleaveapp.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hostelleaveapp.LogInRepositary.GuardLogInRepo
import com.example.hostelleaveapp.ViewModel.GuardSignUpViewModel
import com.example.hostelleaveapp.ViewModel.StudentViewModel.SignUpViewModel

class GuardSignUpFactory(private val repo: GuardLogInRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GuardSignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GuardSignUpViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
