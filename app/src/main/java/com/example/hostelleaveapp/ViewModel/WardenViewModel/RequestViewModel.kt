package com.example.hostelleaveapp.ViewModel.WardenViewModel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelleaveapp.DataModel.LeaveData
import com.example.hostelleaveapp.MainRepositary.WardenRepositary.WardenDataRepo
import kotlinx.coroutines.launch
import javax.inject.Inject

class RequestViewModel @Inject constructor(
    private val repository: WardenDataRepo
) : ViewModel() {

    private val _leaveRequests = MutableLiveData<List<LeaveData>>()
    val leaveRequests: LiveData<List<LeaveData>> = _leaveRequests
    val update_Data = MutableLiveData<Boolean>()

    fun loadLeaveRequests() {
        viewModelScope.launch {
            _leaveRequests.value = repository.fetchLeaveData()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(context: Context,
                   id: String, status: String) {
        viewModelScope.launch {
            val success = repository.updateLeaveStatus(context,id, status)
            update_Data.postValue(success)
            loadLeaveRequests()
        }
    }
}
