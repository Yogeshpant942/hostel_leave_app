package com.example.hostelleaveapp.Ui.Warden

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.LogInRepositary.wardenLogin.WardenLoginRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.StudentViewModel.UiState
import com.example.hostelleaveapp.ViewModel.WardenViewModel.signUpViewmodel
import com.example.hostelleaveapp.ViewModelFactory.Warden.SIgnUpFactory
import com.example.hostelleaveapp.databinding.FragmentWardenLoginBinding

class WardenLoginFragment : Fragment() {
    private lateinit var binding: FragmentWardenLoginBinding
    private lateinit var viewModel: signUpViewmodel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWardenLoginBinding.inflate(inflater, container, false)

        val repo = WardenLoginRepo()
        val factory = SIgnUpFactory(repo)
        viewModel = ViewModelProvider(this, factory)[signUpViewmodel::class.java]

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.SigInUser(email, password)
            }
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    try {
                        findNavController().navigate(R.id.action_wardenLoginFragment_to_wardenMainFragment)
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        return binding.root
    }
}
