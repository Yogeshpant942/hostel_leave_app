package com.example.hostelleaveapp.LogInRepositary.wardenLogin

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.hostelleaveapp.Converters
import com.example.hostelleaveapp.DataModel.StudentData
import com.example.hostelleaveapp.DataModel.WardenData
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary.Companion
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class AuthResultState {
    data class Success(val user: AuthResult) : AuthResultState()
    data class Failure(val message: String) : AuthResultState()
}
class WardenLoginRepo {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    companion object {
        private const val TAG = "WardenRepositary"
    }
    @SuppressLint("SuspiciousIndentation")
    suspend fun signUpwithEmailAndPassword(
        context: Context,
        name: String,
        email: String,
        phoneNo: String,
        password: String,
        ImageUri: String
    ): com.example.hostelleaveapp.LogInRepositary.StudentRepo.AuthResultState {
        Log.d(TAG, "signUpwithEmailAndPassword: Starting signup for $email")

        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Log.d(TAG, "FirebaseAuth success: User UID = ${auth.currentUser?.uid}")
            val uid = auth.currentUser!!.uid
            val studentData = hashMapOf(
                "name" to name,
                "email" to email,
                "phoneNo" to phoneNo,
                "password" to password,
                "image" to ImageUri
            )
            firestore.collection("warden").document(uid).set(studentData).await()
            Log.d(TAG, "Firestore: Student data saved for UID = $uid")
            com.example.hostelleaveapp.LogInRepositary.StudentRepo.AuthResultState.Success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Signup failed: ${e.message}", e)
            com.example.hostelleaveapp.LogInRepositary.StudentRepo.AuthResultState.Failure(
                e.message ?: "Unknown error occurred"
            )
        }
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun fetchStudentDetail(): WardenData? {
        val currentUser = auth.currentUser!!.uid

        return try{
            val snapshot =  firestore.collection("warden").document(currentUser).get().await()
            snapshot.toObject(WardenData::class.java)
        }
        catch (e:Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun LoginWithEmailAndPassword(email:String, password: String): com.example.hostelleaveapp.LogInRepositary.StudentRepo.AuthResultState {
        return  try{

            val res =    auth.signInWithEmailAndPassword(email,password).await()
            com.example.hostelleaveapp.LogInRepositary.StudentRepo.AuthResultState.Success(res)
        }catch (e:Exception){
            Log.e("TAG" , "SignIn failed: ${e.message}", e)
            com.example.hostelleaveapp.LogInRepositary.StudentRepo.AuthResultState.Failure(e.message ?: "Unknown error occurred")
        }



    }

    suspend fun logout():Boolean{
        return try{

            withContext(Dispatchers.IO){
                FirebaseAuth.getInstance().signOut()
                true
            }
        }catch (e:Exception){
            false
        }
    }

}