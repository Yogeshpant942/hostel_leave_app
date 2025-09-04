package com.example.hostelleaveapp.FCM

import com.example.hostelleaveapp.DataModel.FcmNotificationRequest
import com.example.hostelleaveapp.DataModel.LeaveData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface API_Interface {

    @POST("api/notify")
    suspend fun sendNotification(@Body request: FcmNotificationRequest): Response<Void>


    @POST("save-token")
    suspend fun saveToken(@Body request: TokenRequest): Response<Void>


}