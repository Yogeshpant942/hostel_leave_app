package com.example.hostelleaveapp


import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class HostelLeaveApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val app = FirebaseApp.initializeApp(this)
        if (app == null) {
            Log.e("TAG1", "FirebaseApp initialization FAILED")
        } else {
            Log.i("TAG2", "FirebaseApp initialized successfully")
        }
    }
}
