package com.example.hostelleaveapp.MainRepositary.WardenRepositary

import EmailWorker
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import com.example.hostelleaveapp.DataModdel.StudentLeaveDetail
import com.example.hostelleaveapp.DataModel.LeaveData
import com.example.hostelleaveapp.Util.Constants.getDaysBetweenDates
import com.example.hostelleaveapp.Util.EmailSender
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class WardenDataRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "WardenRepo"

    suspend fun fetchLeaveData(): List<LeaveData> {
        val firestore = Firebase.firestore
        val leaveRequests = mutableListOf<LeaveData>()

        try {
            val studentDocs = firestore.collection("studentLeaveApply").get().await()
            Log.d("FetchDebug", "Total students found: ${studentDocs.size()}")

            for (studentDoc in studentDocs.documents) {
                val data = studentDoc.data ?: continue
                val studentId = studentDoc.id

                // Extract leave data directly from the student document
                val leave = LeaveData(
                    name = data["name"] as? String ?: "",
                    start_date = data["start_date"] as? String ?: "",
                    end_date = data["end_date"] as? String ?: "",
                    leaveReason = data["leaveReason"] as? String ?: "",
                    leaveStatus = data["leaveStatus"] as? String ?: "Pending",
                    destination = data["destination"] as? String ?: "",
                    studentId = studentId,
                    email = data["email"] as? String ?: ""
                )

                if (leave.start_date.isNotEmpty()) {
                    leaveRequests.add(leave)
                }
            }

            Log.d("FetchDebug", "Total leave requests fetched: ${leaveRequests.size}")
            return leaveRequests
        } catch (e: Exception) {
            Log.e("FetchDebug", "Error fetching leave data: ${e.message}", e)
            return emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateLeaveStatus(
        context: Context,
        id: String,
        status: String,
    ): Boolean {
        return try {
            Log.d(TAG, "Updating leaveStatus='$status' for id=$id")

            val leaveDocRef = firestore.collection("studentLeaveApply").document(id)
            val leaveDocSnapshot = leaveDocRef.get().await()

            if (leaveDocSnapshot.exists()) {
                leaveDocRef.update("leaveStatus", status).await()
                Log.d(TAG, "Updated leaveStatus for doc: $id")
            } else {
                Log.w(TAG, "No leave document found for id=$id")
            }

            // Fetch email & name from 'students' collection
            val snapshot = firestore.collection("students").document(id).get().await()
            val email = snapshot.getString("email")
            val name = snapshot.getString("name")

            if (email != null && name != null) {
                sendLeaveStatusTOEmail(email, status)
                if (status == "Approved") {
                    sendLeaveEndAlert(context, email, name)
                }
            } else {
                Log.w(TAG, "Email or name is null for studentId=$id")
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update leave status for id=$id: ${e.message}", e)
            false
        }
    }
    private fun sendLeaveStatusTOEmail(email: String, decision: String) {
        val subject = "Hostel Leave Request Update"
        val body = "Hello, your leave request has been $decision by the warden."

        Log.d(TAG, "Sending email to $email with decision=$decision")
        EmailSender.sendEmail(email, subject, body) { success, message ->
            if (success) {
                Log.d(TAG, "Email sent to $email")
            } else {
                Log.e(TAG, "Failed to send email to $email: $message")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun sendLeaveEndAlert(context: Context, email: String, name: String): Boolean {
        return try {
            val snapshot = firestore.collection("GuardRequestStatus").get().await()
            for (data in snapshot.documents) {
                val userData = data.toObject(StudentLeaveDetail::class.java)
                val startDate = userData?.start_date
                val endDate = userData?.end_date
                if (!startDate.isNullOrBlank() && !endDate.isNullOrBlank()) {
                    val remainingDays = getDaysBetweenDates(startDate, endDate)
                    if (remainingDays > 0) {
                        scheduleEmailAfterNDays(context, remainingDays, email, name)
                    }
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error sending end alert: ${e.message}", e)
            false
        }
    }

    private fun scheduleEmailAfterNDays(context: Context, days: Long, email: String, name: String) {
        val input = workDataOf(
            "toEmail" to email,
            "subject" to "Leave Reminder",
            "body" to "Hey $name, your leave has ended. Please return to campus ASAP."
        )
        val request = OneTimeWorkRequestBuilder<EmailWorker>()
            .setInputData(input)
            .setInitialDelay(days, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "LeaveReminder_$email", ExistingWorkPolicy.REPLACE, request
        )

        Log.d(TAG, "Scheduled email to $email after $days day(s)")
    }
}
