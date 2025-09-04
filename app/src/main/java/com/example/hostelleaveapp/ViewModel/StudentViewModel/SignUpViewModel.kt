package com.example.hostelleaveapp.ViewModel.StudentViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.AuthResultState
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary
import kotlinx.coroutines.launch
sealed class UiState {
    data class Success(val message: String): UiState()
    data class Error(val message: String): UiState()
}

class SignUpViewModel(private val repository: StudentRepositary) : ViewModel() {
    val authState = MutableLiveData<UiState>()

    fun loginUser(context: Context,name: String, email: String, phoneNo: String, rollNo: String, password: String,imageUri:String,token:String) {
        viewModelScope.launch {
            when (val result = repository.signUpwithEmailAndPassword(
                context,
                name,
                email,
                phoneNo,
                rollNo,
                password,
                imageUri,
                token
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

}
