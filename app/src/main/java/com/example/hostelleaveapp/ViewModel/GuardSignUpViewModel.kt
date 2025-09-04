package com.example.hostelleaveapp.ViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelleaveapp.LogInRepositary.GuardAuthResultState
import com.example.hostelleaveapp.LogInRepositary.GuardLogInRepo
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.AuthResultState
import kotlinx.coroutines.launch

sealed class GuardUiState {
    data class Success(val message: String): GuardUiState()
    data class Error(val message: String): GuardUiState()
}
class GuardSignUpViewModel (private val repository: GuardLogInRepo) : ViewModel() {
    val authState = MutableLiveData<GuardUiState>()
    fun signUpGuard(
        context: Context,
        name: String,
        email: String,
        phoneNo: String,
        password: String,
        imageUri: String,
        guradId:String
    ) {
        viewModelScope.launch {
            when (val result = repository.signUpWithEmailAndPassword(context, name, email, phoneNo, password, imageUri,guradId)) {
                is GuardAuthResultState.Success -> {
                    authState.postValue(GuardUiState.Success("Sign Up Success"))
                }
                is GuardAuthResultState.Failure -> {
                    authState.postValue(GuardUiState.Error("Sign Up Failed: ${result.message}"))
                }
            }
        }
    }
    fun SigInUser(email: String,password: String){
        viewModelScope.launch {
            when (val res = repository.LoginWithEmailAndPassword(email,password)){
                is AuthResultState.Success->{
                    authState.postValue(GuardUiState.Success("Sign In Success"))
                }
                is AuthResultState.Failure -> {
                    authState.postValue(GuardUiState.Error("Sign In Failed: ${res.message}"))
                }
            }
        }
    }
}