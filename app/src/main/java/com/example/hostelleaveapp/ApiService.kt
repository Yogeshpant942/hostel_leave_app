package com.example.hostelleaveapp

import com.example.hostelleaveapp.DataModel.CompareResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("facepp/v3/compare")
    fun compareFaces(
        @Field("api_key") apiKey: String,
        @Field("api_secret") apiSecret: String,
        @Field("image_base64_1") imageBase64_1: String,
        @Field("image_base64_2") imageBase64_2: String
    ): Call<CompareResponse>

}