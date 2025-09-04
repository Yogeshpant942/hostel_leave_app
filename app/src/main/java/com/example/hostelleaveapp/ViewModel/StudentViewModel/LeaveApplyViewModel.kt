package com.example.hostelleaveapp.ViewModel.StudentViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelleaveapp.DataModel.StudentData
import com.example.hostelleaveapp.MainRepositary.StudentMainRepo.StudentMainDataRepo
import kotlinx.coroutines.launch

class LeaveApplyViewModel(private val repo: StudentMainDataRepo) : ViewModel() {
    val leaveApplyStatus = MutableLiveData<Boolean>()
    val Student_detail = MutableLiveData<StudentData?>()
    val logoutStatus = MutableLiveData<Boolean>()
    val count_leave = MutableLiveData<Int>()
    fun leaveDataApply(
        startDate: String,
        endDate: String,
        leaveReason: String,
        status: String,
        destination: String,
    ) {
        viewModelScope.launch {
            try {
                val result = repo.StoreLeaveData(startDate, endDate, leaveReason, status, destination)
                leaveApplyStatus.postValue(result)
            } catch (e: Exception) {
                leaveApplyStatus.postValue(false)
            }
        }
    }

    fun getStudentDetail() {
        viewModelScope.launch {
            try {
                val result: StudentData? = repo.fetchStudentDetail()
                Student_detail.postValue(result)
            } catch (e: Exception) {
                Student_detail.postValue(null)
            }
        }
    }

    fun logOutUser() {
        viewModelScope.launch {
            try {
                val result = repo.logOutUser()
                logoutStatus.postValue(result)
            } catch (e: Exception) {
                logoutStatus.postValue(false)
            }
        }
    }

    fun fetchLeaveCount(){
        viewModelScope.launch {
            val res = repo.countLeaves()
            count_leave.postValue(res)
        }
    }



}
