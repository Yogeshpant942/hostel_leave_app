package com.example.hostelleaveapp.DataModel
import com.google.gson.annotations.SerializedName

data class CompareResponse(
@SerializedName("confidence")
val confidence: Float? = null,

@SerializedName("thresholds")
val thresholds: Map<String, Float>? = null,
        
@SerializedName("request_id")
val requestId: String? = null,

@SerializedName("time_used")
val timeUsed: Int? = null,

@SerializedName("error_message")
val errorMessage: String? = null
)


