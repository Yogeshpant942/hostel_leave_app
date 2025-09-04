package com.example.hostelleaveapp.ViewModel.WardenViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelleaveapp.DataModel.WardenData
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.AuthResultState
import com.example.hostelleaveapp.LogInRepositary.wardenLogin.WardenLoginRepo
import com.example.hostelleaveapp.ViewModel.StudentViewModel.UiState
import kotlinx.coroutines.launch

class signUpViewmodel (private val repository: WardenLoginRepo) : ViewModel() {

    val authState = MutableLiveData<UiState>()
    val Student_detail = MutableLiveData<WardenData?>()
    val logOutstatus = MutableLiveData<Boolean>()

    fun getWardenDetail() {
        viewModelScope.launch {
            try {
                val result: WardenData? = repository.fetchStudentDetail()
                Student_detail.postValue(result)
            } catch (e: Exception) {
                Student_detail.postValue(null)
            }
        }
    }

    fun loginUser(context: Context, name: String, email: String, phoneNo: String,  password: String, imageUri: String) {
        viewModelScope.launch {
            when (val result = repository.signUpwithEmailAndPassword(
                context,
                name,
                email,
                phoneNo,
                password,
                imageUri
            )) {
                is AuthResultState.Success -> {
                    authState.postValue(UiState.Success("Sign Up Success"))
                }

                is AuthResultState.Failure -> {
                    authState.postValue(UiState.Error("Sign Up Failed: ${result.message}"))
                }
            }
    }
    }
    fun SigInUser(email: String,password: String){
        viewModelScope.launch {
            when (val res = repository.LoginWithEmailAndPassword(email,password)){
                is AuthResultState.Success->{
                    authState.postValue(UiState.Success("Sign In Success"))
                }
                is AuthResultState.Failure -> {
                    authState.postValue(UiState.Error("Sign In Failed: ${res.message}"))
                }
            }
        }
    }

      fun logOutUser(){
          viewModelScope.launch {
              logOutstatus.postValue(repository.logout())
          }
      }
}