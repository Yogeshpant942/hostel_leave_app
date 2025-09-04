package com.example.hostelleaveapp.StartingInterface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.hostelleaveapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SignupOptionBottomSheet(private val onOptionSelected: (String) -> Unit
) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
): View {
    val view = inflater.inflate(R.layout.fragment_signup_option_bottom_sheet, container, false)

    view.findViewById<TextView>(R.id.signupWarden).setOnClickListener {
        onOptionSelected("Warden")
        dismiss()
    }

    view.findViewById<TextView>(R.id.signupStudent).setOnClickListener {
        onOptionSelected("Student")
        dismiss()
    }

    view.findViewById<TextView>(R.id.signupGuard).setOnClickListener {
        onOptionSelected("Guard")
        dismiss()
    }

    return view
}
}
