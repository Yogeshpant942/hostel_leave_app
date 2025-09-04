package com.example.hostelleaveapp.ViewModel.StudentViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelleaveapp.DataModel.LeaveData
import com.example.hostelleaveapp.DataModel.StudentData
import com.example.hostelleaveapp.MainRepositary.StudentMainRepo.StudentMainDataRepo
import kotlinx.coroutines.launch

class LeaveHistoryViewModel(private val repo:StudentMainDataRepo):ViewModel(){
    val leave_data = MutableLiveData<List<LeaveData>>()

    fun fetchLeaveHistory(){
        viewModelScope.launch {
            try{
                val result = repo.fetchLeaveDetail()
                leave_data.postValue(result)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}