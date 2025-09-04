package com.example.hostelleaveapp.FCM

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hostelleaveapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService:FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM","New Token:$token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        showNotification(title?:"Notification",message?:"")
    }

    private fun showNotification(title:String,message:String){
        val builder = NotificationCompat.Builder(this,"fcm_channel")
            .setSmallIcon(R.drawable.circle_lightamber)
            .setContentText(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0,builder.build())
    }

}