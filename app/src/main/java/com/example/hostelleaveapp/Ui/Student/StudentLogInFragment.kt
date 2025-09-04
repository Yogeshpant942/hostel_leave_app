package com.example.hostelleaveapp.Ui.Student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.StudentViewModel.SignUpViewModel
import com.example.hostelleaveapp.ViewModel.StudentViewModel.UiState
import com.example.hostelleaveapp.ViewModelFactory.Student.SignUpFactory
import com.example.hostelleaveapp.databinding.FragmentStudentLogInBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StudentLogInFragment : Fragment() {
    private lateinit var binding: FragmentStudentLogInBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentLogInBinding.inflate(inflater, container, false)

        val repo = StudentRepositary()
        val factory = SignUpFactory(repo)
        viewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val iiituPattern = Regex("^[a-z0-9]{4,}@iiitu\\.ac\\.in$")
            if (!iiituPattern.matches(email)) {
                Toast.makeText(requireContext(), "Enter a valid IIITU email (e.g., xxxx@iiitu.ac.in)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.SigInUser(email, password)
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(100)
                        if (isAdded && view != null) {
                            try {
                                findNavController().navigate(R.id.action_studentLogInFragment_to_studentMainFragment)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(requireContext(), "Navigation failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_studentLogInFragment_to_studnetSignUpFragment)
        }

        return binding.root
    }
}
