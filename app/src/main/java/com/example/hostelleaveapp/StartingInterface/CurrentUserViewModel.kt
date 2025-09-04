package com.example.hostelleaveapp.StartingInterface

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CurrentUserViewModel: ViewModel() {

    val userRole = MutableLiveData<String?>()
    private val db = FirebaseFirestore.getInstance()

    fun fetchCurrentUser(uid: String) {
        db.collection("warden").document(uid).get()
            .addOnSuccessListener { wardenDoc ->
                if (wardenDoc.exists()) {
                    userRole.value = "warden"
                } else {
                    db.collection("students").document(uid).get()
                        .addOnSuccessListener { studentDoc ->
                            if (studentDoc.exists()) {
                                userRole.value = "student"
                            } else {
                                db.collection("guards").document(uid).get()
                                    .addOnSuccessListener { guardDoc ->
                                        if (guardDoc.exists()) {
                                            userRole.value = "guard"
                                        } else {
                                            userRole.value = null
                                        }
                                    }
                            }
                        }
                }
            }
            .addOnFailureListener {
                userRole.value = null
            }
    }
}
