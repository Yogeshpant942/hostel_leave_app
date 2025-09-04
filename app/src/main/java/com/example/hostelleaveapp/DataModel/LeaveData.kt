package com.example.hostelleaveapp.DataModel
data class LeaveData(
    val name: String = "",
    val start_date: String = "",
    val end_date: String = "",
    val leaveReason: String = "",
    val leaveStatus: String = "Pending",
    val studentId: String = "",
    val destination: String = "",
    val email: String? = null,

    )
