package com.example.hostelleaveapp.Ui.Guard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.checkerframework.common.subtyping.qual.Bottom

class GuardBarCodeOptions(private val OnOptionSelect:(String)->Unit):BottomSheetDialogFragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_guard_barcode_option_sheet, container, false)

        view.findViewById<TextView>(R.id.Out).setOnClickListener {
            OnOptionSelect("Out")
            dismiss()
        }
        view.findViewById<TextView>(R.id.In).setOnClickListener {
            OnOptionSelect("In")
            dismiss()
        }
        return view
    }

}