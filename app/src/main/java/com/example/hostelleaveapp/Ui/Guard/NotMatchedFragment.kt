package com.example.hostelleaveapp.Ui.Guard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.hostelleaveapp.databinding.FragmentNotMatchedBinding

class NotMatchedFragment : DialogFragment() {

     lateinit var binding:FragmentNotMatchedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotMatchedBinding.inflate(layoutInflater,container,false)
        return binding.root
    }
}