package com.example.hostelleaveapp.LogInRepositary

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.hostelleaveapp.Converters
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.AuthResultState
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary.Companion
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class GuardAuthResultState {
    data class Success(val user: AuthResult) : GuardAuthResultState()
    data class Failure(val message: String) : GuardAuthResultState()
}
class GuardLogInRepo {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    companion object {
        private const val TAG = "GuardRepository"
    }
    suspend fun signUpWithEmailAndPassword(
        context: Context,
        name: String,
        email: String,
        phoneNo: String,
        password: String,
        imageUri: String,
        guardId:String
    ): GuardAuthResultState {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser!!.uid
            val guardData = hashMapOf(
                "name" to name,
                "email" to email,
                "phoneNo" to phoneNo,
                "password" to password,
                "profileImage" to imageUri,
                 "guardId" to guardId)
            firestore.collection("guards").document(uid).set(guardData).await()
            Log.d(TAG, "Guard signed up and data saved.")
            GuardAuthResultState.Success(result)
        } catch (e: Exception) {
            Log.e(TAG, "SignUp failed: ${e.message}", e)
            GuardAuthResultState.Failure(e.message ?: "Unknown error occurred")
        }
    }
    suspend fun LoginWithEmailAndPassword(email:String, password: String): AuthResultState {
        return  try{
            val res =  auth.signInWithEmailAndPassword(email,password).await()
            AuthResultState.Success(res)
        }catch (e:Exception){
            Log.e("TAG", "SignIn failed: ${e.message}", e)
            AuthResultState.Failure(e.message ?: "Unknown error occurred")
        }
    }
}