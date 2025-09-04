package com.example.hostelleaveapp.Ui.Warden

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.Converters.compressImage
import com.example.hostelleaveapp.Converters.uriToByteArray
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary
import com.example.hostelleaveapp.LogInRepositary.wardenLogin.WardenLoginRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.WardenViewModel.signUpViewmodel
import com.example.hostelleaveapp.ViewModel.StudentViewModel.UiState
import com.example.hostelleaveapp.ViewModelFactory.Student.SignUpFactory
import com.example.hostelleaveapp.ViewModelFactory.Warden.SIgnUpFactory
import com.example.hostelleaveapp.databinding.FragmentWardenSignUpBinding

class WardenSignUpFragment : Fragment() {
    lateinit var viewModel: signUpViewmodel
    lateinit var binding: FragmentWardenSignUpBinding
    var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWardenSignUpBinding.inflate(inflater, container, false)

        val repo = WardenLoginRepo()
        val factory = SIgnUpFactory(repo)
        viewModel = ViewModelProvider(this, factory)[signUpViewmodel::class.java]
        binding.profileImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()
            val phoneNo = binding.PhoneNo.text.toString()
            val pattern = Regex("\\d+@Wardeniiitu\\.ac\\.in$")
            if (!pattern.matches(email)) {
                Toast.makeText(requireContext(), "Enter a valid email like 12345@Wardeniiitu.ac.in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isNotEmpty() && password.isNotEmpty() && phoneNo.isNotEmpty() && name.isNotEmpty()  && imageUri != null) {
                val imageBytes = uriToByteArray(requireContext(),imageUri!!)
                val compressBytes = compressImage(imageBytes!!)
                val base64String = Base64.encodeToString(compressBytes, Base64.DEFAULT)
                Log.d("Signup", "Base64 length before saving: ${base64String.length}")
                viewModel.loginUser(requireContext(),name, email, phoneNo, password,base64String)
            } else {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    try {
                        findNavController().navigate(R.id.action_wardenSignUpFragment_to_wardenMainFragment)
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                is UiState.Error -> {
                    Toast.makeText (requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        return binding.root
    }

    private val  imagePicker= registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
        if(uri!= null){
            binding.profileImage.setImageURI(uri)
            imageUri = uri
        }
    }



}