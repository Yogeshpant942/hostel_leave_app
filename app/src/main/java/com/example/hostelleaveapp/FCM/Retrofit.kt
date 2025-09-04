package com.example.hostelleaveapp.FCM

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    private const val BASE_URL = "http:/192.168.151.70:8080/"

    val apiInterface: API_Interface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(API_Interface::class.java)
    }
}