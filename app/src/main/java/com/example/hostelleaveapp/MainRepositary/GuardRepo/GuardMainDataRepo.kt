package com.example.hostelleaveapp.MainRepositary.GuardRepo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import com.example.hostelleaveapp.DataModdel.StudentLeaveDetail
import com.example.hostelleaveapp.DataModel.CombinedStudentData
import com.example.hostelleaveapp.DataModel.GuardData
import com.example.hostelleaveapp.Util.Constants.getDaysBetweenDates
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private const val TAG = "GuardMainDataRepo"

class GuardMainDataRepo {

    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    @SuppressLint("SuspiciousIndentation")
    suspend fun fetchStudentDate(rollNo: String): CombinedStudentData {
        var uid = ""

        val snapshot = firebaseFirestore.collection("students").get().await()
        for (data in snapshot.documents) {
            val tempRollNo = data.getString("rollNo")
            if (tempRollNo == rollNo) {
                uid = data.id
                break
            }
        }

        if (uid.isEmpty()) {
            Log.e(TAG, "No student found with roll number: $rollNo")
            throw IllegalArgumentException("Student with roll number $rollNo not found.")
        }

        Log.d(TAG, "Matched UID: $uid")

        val studentdetailSnapshot =
            firebaseFirestore.collection("students").document(uid).get().await()
        val leaveDetailSnapshot =
            firebaseFirestore.collection("studentLeaveApply").document(uid).get().await()

        val studentDetails = studentdetailSnapshot.toObject(StudentLeaveDetail::class.java)
        val leaveDetails = leaveDetailSnapshot.toObject(StudentLeaveDetail::class.java)

        if (studentDetails != null) {
            Log.d(
                TAG,
                "Fetched Student Details -> Name: ${studentDetails.name}, Roll No: ${studentDetails.rollNo}, Email: ${studentDetails.email}, Phone: ${studentDetails.PhoneNo}"
            )
        } else {
            Log.e(TAG, "Student details are null")
        }

        if (leaveDetails != null) {
            Log.d(
                TAG,
                "Fetched Leave Details -> Start: ${leaveDetails.start_date}, End: ${leaveDetails.end_date}, Reason: ${leaveDetails.leaveReason}, Status: ${leaveDetails.leaveStatus}"
            )
        } else {
            Log.e(TAG, "Leave details are null")
        }

        return CombinedStudentData(
            studentDetails = studentDetails,
            leaveDetails = leaveDetails
        )
    }

    suspend fun updateStudentStatus(
        name: String,
        rollNo: String,
        PhoneNo: String,
        status: String,
        leaveReason: String,
        startDate: String,
        endDate: String
    ): Boolean {
        return try {
            val data = StudentLeaveDetail(
                name = name,
                rollNo = rollNo,
                PhoneNo = PhoneNo,
                leaveStatus = status,
                leaveReason = leaveReason,
                start_date = startDate,
                end_date = endDate
            )
            firebaseFirestore.collection("GuardRequestStatus").add(data).await()
            Log.d(TAG, "Successfully added student status: $data")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding student status: ${e.message}")
            false
        }
    }

    suspend fun fetchStudentStatus(): List<StudentLeaveDetail> {
        return try {
            val snapshot = firebaseFirestore.collection("GuardRequestStatus").get().await()
            Log.d(TAG, "Fetched ${snapshot.size()} documents from GuardRequestStatus")

            val resultList = snapshot.documents.mapNotNull { document ->
                val student = document.toObject(StudentLeaveDetail::class.java)
                Log.d(TAG, "Parsed StudentLeaveDetail: $student")
                student
            }

            Log.d(TAG, "Total valid parsed student records: ${resultList.size}")
            resultList

        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch student status: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun checkOutOrNot(rollNo: String): Boolean {
        var exist = false
        var uid = ""
        val snapshot = firebaseFirestore.collection("GuardRequestStatus").get().await()
        for (data in snapshot.documents) {
            val studentLeaveData = data.toObject(StudentLeaveDetail::class.java)
            val tempRollNo = studentLeaveData?.rollNo
            if (tempRollNo == rollNo) {
                exist = true
                uid = data.id
                break
            }
        }
        return if (exist) {
            removeUserFromList(uid)
            true
        } else {
            false
        }
    }

    suspend fun removeUserFromList(uid: String) {
        try {
            firebaseFirestore.collection("GuardRequestStatus")
                .document(uid)
                .delete()
                .await()
            Log.d("Firestore", "User $uid removed successfully")
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to remove user: ${e.message}")
        }
    }
    @SuppressLint("SuspiciousIndentation")
    suspend fun fetchGuardData(): GuardData? {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        val TAG = "FetchGuardData"

        if (currentUserUid == null) {
            Log.e(TAG, "Current user UID is null")
            return null
        }

        return try {
            Log.d(TAG, "Fetching guard data for UID: $currentUserUid")

            val snapshot = firebaseFirestore.collection("guards")
                .document(currentUserUid)
                .get()
                .await()

            if (snapshot.exists()) {
                val data = snapshot.toObject(GuardData::class.java)
                Log.d(TAG, "Fetched guard data: $data")
                data
            } else {
                Log.e(TAG, "No document found for UID: $currentUserUid")
                null
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching guard data: ${e.message}", e)
            null
        }
    }





}