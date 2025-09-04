package com.example.hostelleaveapp.MainRepositary.StudentMainRepo

import android.annotation.SuppressLint
import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import com.example.hostelleaveapp.DataModel.LeaveData
import com.example.hostelleaveapp.DataModel.StudentData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale


class StudentMainDataRepo() {
    private val auth :FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore:FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object{
        private const val TAG ="StudentMainDataRepo"
    }
    suspend fun StoreLeaveData(
        startDate: String,
        endDate: String,
        leaveReason: String,
        status: String,
        destination: String
    ): Boolean {
        val currentUser = auth.currentUser!!.uid

        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parseDate = dateFormat.parse(startDate)
            val calendar = Calendar.getInstance().apply {
                time = parseDate
            }
            val targetMonth = calendar.get(Calendar.MONTH)
            val targetYear = calendar.get(Calendar.YEAR)

            val userDocRef = firestore.collection("studentLeaveApply").document(currentUser)

            val snapshot = userDocRef.get().await()

            if (snapshot.exists()) {
                val existingStatus = snapshot.getString("leaveStatus") ?: ""
                val existingStart = snapshot.getString("start_date") ?: ""

                if (existingStatus.equals("Pending", ignoreCase = true) ||
                    existingStatus.equals("Approved", ignoreCase = true)
                ) {
                    Log.w("LeaveStore", "User already has active leave")
                    return false
                }

                // Limit to 4 leaves per month
                val existingMonthYearMatch = try {
                    val docDate = dateFormat.parse(existingStart)
                    val docCal = Calendar.getInstance().apply { time = docDate }
                    docCal.get(Calendar.MONTH) == targetMonth &&
                            docCal.get(Calendar.YEAR) == targetYear
                } catch (e: Exception) {
                    false
                }

                if (existingMonthYearMatch) {
                    val leaveCount = snapshot.getLong("monthlyLeaveCount") ?: 1L
                    if (leaveCount >= 4) {
                        Log.w("LeaveStore", "Max 4 leaves per month exceeded")
                        return false
                    }
                    userDocRef.update("monthlyLeaveCount", leaveCount + 1).await()
                } else {
                    // Reset for new month
                    userDocRef.update("monthlyLeaveCount", 1).await()
                }
            }

            // New or updated leave data
            val leaveData = mapOf(
                "start_date" to startDate,
                "end_date" to endDate,
                "leaveReason" to leaveReason,
                "leaveStatus" to status,
                "destination" to destination,
                "studentId" to currentUser,
                "monthlyLeaveCount" to 1
            )

            userDocRef.set(leaveData).await()

            Log.d("LeaveStore", "Leave request saved directly in student doc")
            true

        } catch (e: Exception) {
            Log.e("LeaveStore", "Error storing leave: ${e.message}", e)
            false
        }
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun fetchStudentDetail():StudentData? {
        val currentUser = auth.currentUser!!.uid

        return try{
         val snapshot =  firestore.collection("students").document(currentUser).get().await()
            snapshot.toObject(StudentData::class.java)
        }
        catch (e:Exception){
            e.printStackTrace()
            null
        }
    }
    suspend fun fetchLeaveDetail(): MutableList<LeaveData> {
        val currentUser = auth.currentUser!!.uid
        val studentList = mutableListOf<LeaveData>()
        Log.d("LeaveRepo", "Fetching leave details for user UID: $currentUser")

        return try {
            val snapshot = firestore.collection("studentLeaveApply").document(currentUser).get().await()
            Log.d("LeaveRepo", "Document fetched: ${snapshot.exists()}")

            snapshot.let {
                val data = it.toObject(LeaveData::class.java)
                if (data != null) {
                    Log.d("LeaveRepo", "Leave Data: $data")
                    studentList.add(data)
                } else {
                    Log.w("LeaveRepo", "No leave data found in document")
                }
            }

            Log.d("LeaveRepo", "Total leave entries fetched: ${studentList.size}")
            studentList
        } catch (e: Exception) {
            Log.e("LeaveRepo", "Error fetching leave details: ${e.message}", e)
            mutableListOf()
        }
    }

    suspend fun logOutUser():Boolean{
        return try {
        withContext(Dispatchers.IO){
        FirebaseAuth.getInstance().signOut()
        }
            true
        }catch (e:Exception)
        {
            false
        }
    }

    suspend fun countLeaves():Int{
        val currentUser = auth.currentUser!!.uid
        return try{
            val snapshot = firestore.collection("studentLeaveApply").document(currentUser).collection("leave").get().await()
            val count = snapshot.size()
            Log.d("count", count.toString())
            count
        }catch (e:Exception){
            e.printStackTrace()
            0
        }

    }

}