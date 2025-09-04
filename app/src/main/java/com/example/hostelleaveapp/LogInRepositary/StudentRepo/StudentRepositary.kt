package com.example.hostelleaveapp.LogInRepositary.StudentRepo

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.hostelleaveapp.Converters
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class AuthResultState {
    data class Success(val user: AuthResult) : AuthResultState()
    data class Failure(val message: String) : AuthResultState()
}

class StudentRepositary {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    companion object {
        private const val TAG = "StudentRepositary"
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun signUpwithEmailAndPassword(
        context: Context,
        name: String,
        email: String,
        phoneNo: String,
        rollNo: String,
        password: String,
        Image: String,
        token:String
    ): AuthResultState {
        Log.d(TAG, "signUpwithEmailAndPassword: Starting signup for $email")

        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Log.d(TAG, "FirebaseAuth success: User UID = ${auth.currentUser?.uid}")
            val uid = auth.currentUser!!.uid
            val studentData = hashMapOf(
                "name" to name,
                "email" to email,
                "phoneNo" to phoneNo,
                "rollNo" to rollNo,
                "password" to password,
                "profileImage" to Image,
                 "token" to token
            )
            firestore.collection("students").document(uid).set(studentData).await()
            Log.d(TAG, "Firestore: Student data saved for UID = $uid")

            AuthResultState.Success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Signup failed: ${e.message}", e)
            AuthResultState.Failure(e.message ?: "Unknown error occurred")
        }
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun LoginWithEmailAndPassword(email: String, password: String): AuthResultState {
        Log.d(TAG, "Attempting login for email: $email")

        return try {
            val res = auth.signInWithEmailAndPassword(email, password).await()
            Log.d(TAG, "Login successful for email: $email")
            AuthResultState.Success(res)
        } catch (e: Exception) {
            Log.e(TAG, "SignIn failed for email: $email - ${e.message}", e)
            AuthResultState.Failure(e.message ?: "Unknown error occurred")
        }
    }


}

