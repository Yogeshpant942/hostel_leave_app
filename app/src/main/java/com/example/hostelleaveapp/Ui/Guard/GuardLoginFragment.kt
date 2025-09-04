package com.example.hostelleaveapp.Ui.Guard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.LogInRepositary.GuardLogInRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.GuardSignUpViewModel
import com.example.hostelleaveapp.ViewModel.GuardUiState
import com.example.hostelleaveapp.ViewModel.StudentViewModel.UiState
import com.example.hostelleaveapp.ViewModelFactory.GuardSignUpFactory
import com.example.hostelleaveapp.databinding.FragmentGuardLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GuardLoginFragment : Fragment() {
    lateinit var binding:FragmentGuardLoginBinding
   lateinit var viewModel:GuardSignUpViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGuardLoginBinding.inflate(layoutInflater, container, false)

        val repo = GuardLogInRepo()
        val factory = GuardSignUpFactory(repo)
        viewModel = ViewModelProvider(this, factory)[GuardSignUpViewModel::class.java]

        viewModel.authState.removeObservers(viewLifecycleOwner)
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GuardUiState.Success -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(100)
                        if (isAdded && view != null) {
                            try {
                                findNavController().navigate(R.id.action_guardLoginFragment_to_guardMainScreenFragment)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                is GuardUiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // 👇 Login button click listener
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.SigInUser(email, password)
            }
        }

        // Sign-up link click
        binding.tvSignUpLink.setOnClickListener {
            findNavController().navigate(R.id.action_guardLoginFragment_to_guardSignUpFragment)
        }

        return binding.root
    }
}