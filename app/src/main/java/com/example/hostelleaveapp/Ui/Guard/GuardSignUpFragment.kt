package com.example.hostelleaveapp.Ui.Guard

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.Converters.compressImage
import com.example.hostelleaveapp.Converters.uriToByteArray
import com.example.hostelleaveapp.LogInRepositary.GuardLogInRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.GuardSignUpViewModel
import com.example.hostelleaveapp.ViewModel.GuardUiState
import com.example.hostelleaveapp.ViewModelFactory.GuardSignUpFactory
import com.example.hostelleaveapp.databinding.FragmentGuardSignUpBinding

class GuardSignUpFragment : Fragment() {

    private lateinit var viewModel: GuardSignUpViewModel
    private lateinit var binding: FragmentGuardSignUpBinding
    private var imageUri: Uri? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.profileImage.setImageURI(uri)
            imageUri = uri
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGuardSignUpBinding.inflate(inflater, container, false)

        val repo = GuardLogInRepo()
        val factory = GuardSignUpFactory(repo)
        viewModel = ViewModelProvider(this, factory)[GuardSignUpViewModel::class.java]

        binding.profileImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        binding.btnSignUp.setOnClickListener {
            val name = binding.etGuardName.text.toString()
            val email = binding.etEmail.text.toString()
            val phoneNo = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            val guardId = binding.etEmployeeId.text.toString()

            val pattern = Regex("\\d+@guardiiitu\\.ac\\.in$")
            if (!pattern.matches(email)) {
                Toast.makeText(requireContext(), "Enter a valid email like 12345@guardiiitu.ac.in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val imageBytes = uriToByteArray(requireContext(),imageUri!!)
            val compressBytes = compressImage(imageBytes!!)
            val base64String = Base64.encodeToString(compressBytes, Base64.DEFAULT)
            if (name.isNotEmpty() && email.isNotEmpty() && phoneNo.isNotEmpty() && password.isNotEmpty() && base64String != null && guardId.isNotEmpty()) {
                viewModel.signUpGuard(requireContext(), name, email, phoneNo, password, base64String,guardId)
            } else {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GuardUiState.Success -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    try {
                        findNavController().navigate(R.id.action_guardSignUpFragment_to_guardMainScreenFragment)
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                is GuardUiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        return binding.root
    }
}
