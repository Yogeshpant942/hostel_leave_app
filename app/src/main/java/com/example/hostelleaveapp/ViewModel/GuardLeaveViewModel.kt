package com.example.hostelleaveapp.ViewModel

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelleaveapp.DataModdel.StudentLeaveDetail
import com.example.hostelleaveapp.DataModel.CombinedStudentData
import com.example.hostelleaveapp.DataModel.GuardData
import com.example.hostelleaveapp.MainRepositary.GuardRepo.GuardMainDataRepo
import kotlinx.coroutines.launch

class GuardLeaveViewModel(private val repo: GuardMainDataRepo) : ViewModel() {

    val student_leaveStatus = MutableLiveData<CombinedStudentData>()
    val guardStatus = MutableLiveData<Boolean>()
    val checkExistOrNOt = MutableLiveData<Boolean>()
    val guardLeaveStatus = MutableLiveData<List<StudentLeaveDetail>>()
    val guardData = MutableLiveData<GuardData?>()
    val getImage = MutableLiveData<Bitmap>()
    private val TAG = "GuardLeaveViewModel"

    fun fetch_Stdent_leave_Status(rollNo: String) {
        Log.d(TAG, "Fetching student leave status for rollNo: $rollNo")
        viewModelScope.launch {
            val data = repo.fetchStudentDate(rollNo)
            if (data != null) {
                Log.d(TAG, "Student leave status fetched: $data")
            } else {
                Log.d(TAG, "No leave status found for student with rollNo: $rollNo")
            }
            student_leaveStatus.postValue(data)
        }
    }
    @SuppressLint("SuspiciousIndentation")
    fun upload_leaveStatus(
        name: String,
        rollNo: String,
        PhoneNo: String,
        status: String,
        leaveReason: String,
        startDate: String,
        endDate: String
    ) {
        Log.d(TAG, "Uploading leave status for $name ($rollNo)")
        viewModelScope.launch {
            val res = repo.updateStudentStatus(name, rollNo, PhoneNo, status, leaveReason, startDate, endDate)
            Log.d(TAG, "Upload result: $res")
            guardStatus.postValue(res)
        }
    }

    fun fetch_leave_Status_guard() {
        Log.d(TAG, "Fetching all leave requests for guard")
        viewModelScope.launch {
            val res = repo.fetchStudentStatus()
            Log.d(TAG, "Leave requests fetched: ${res.size}")
            guardLeaveStatus.postValue(res)
        }
    }

    fun fetchStudentExist(rollNo: String) {
        Log.d(TAG, "Checking if student with rollNo $rollNo exists or is currently out")
        viewModelScope.launch {
            val result = repo.checkOutOrNot(rollNo)
            Log.d(TAG, "Existence check result: $result")
            checkExistOrNOt.postValue(result)
        }
    }

    fun fetchGuardDetail() {
        Log.d(TAG, "Fetching current guard details")
        viewModelScope.launch {
            val result = repo.fetchGuardData()
            if (result != null) {
                Log.d(TAG, "Guard details: ${result.name}, ID: ${result.guardId}")
            } else {
                Log.d(TAG, "Guard data fetch returned null")
            }
            guardData.postValue(result)
        }
    }

}
