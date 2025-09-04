package com.example.hostelleaveapp.StartingInterface

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging


class SplashScreenFragment : Fragment() {
    lateinit var binding :FragmentSplashScreenBinding
    lateinit var viewModel:CurrentUserViewModel
    private val auth = FirebaseAuth.getInstance()
    private var hasNavigated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentSplashScreenBinding.inflate(layoutInflater,container,false)

        viewModel = ViewModelProvider(this)[CurrentUserViewModel::class.java]

        binding.getStartedBtn.setOnClickListener {
            val curentuser = auth.currentUser
            if(curentuser == null){
                findNavController().navigate(R.id.action_splashScreenFragment_to_roleSelectFragment)
            }
            else{
                viewModel.fetchCurrentUser(curentuser.uid)
            }
        }
        viewModel.userRole.observe(viewLifecycleOwner) { role ->
            if (hasNavigated) return@observe

            when (role) {
                "warden" -> {
                    hasNavigated = true
                    findNavController().navigate(R.id.action_splashScreenFragment_to_wardenMainFragment)
                }
                "student" -> {
                    hasNavigated = true
                    findNavController().navigate(R.id.action_splashScreenFragment_to_studentMainFragment)
                }
                "guard" -> {
                    hasNavigated = true
                    findNavController().navigate(R.id.action_splashScreenFragment_to_guardMainScreenFragment)
                }
                else -> {
                    hasNavigated = true
                    findNavController().navigate(R.id.action_splashScreenFragment_to_roleSelectFragment)
                }
            }
        }

        return binding.root
    }


}