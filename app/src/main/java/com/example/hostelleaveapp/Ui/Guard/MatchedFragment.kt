package com.example.hostelleaveapp.Ui.Guard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.databinding.FragmentMatchedBinding

class MatchedFragment : DialogFragment() {
    lateinit var binding:FragmentMatchedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_matched, container, false)
    }

}