package com.example.hostelleaveapp.Util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
object Constants {
    const val email = "23368wardeniiitu@gmail.com"
    const val password = "kppa qwji psnv vnfn"
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDaysBetweenDates(startDateStr:String, endDateStr:String):Long{
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val startDate = LocalDate.parse(startDateStr,formatter)
        val endDate = LocalDate.parse(endDateStr,formatter)

        return ChronoUnit.DAYS.between(startDate,endDate)
    }
}