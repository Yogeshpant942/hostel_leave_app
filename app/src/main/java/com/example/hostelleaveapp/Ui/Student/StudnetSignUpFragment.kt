package com.example.hostelleaveapp.Ui.Student

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.Converters.compressImage
import com.example.hostelleaveapp.Converters.uriToByteArray
import com.example.hostelleaveapp.LogInRepositary.StudentRepo.StudentRepositary
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.StudentViewModel.SignUpViewModel
import com.example.hostelleaveapp.ViewModel.StudentViewModel.UiState
import com.example.hostelleaveapp.ViewModelFactory.Student.SignUpFactory
import com.example.hostelleaveapp.databinding.FragmentStudnetSignUpBinding
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudnetSignUpFragment : Fragment() {
    lateinit var viewModel: SignUpViewModel
    lateinit var binding: FragmentStudnetSignUpBinding
     var imageUri: Uri? = null
    private var Student_token =  ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudnetSignUpBinding.inflate(inflater, container, false)
        getToken()

        val repo = StudentRepositary()
        val factory = SignUpFactory(repo)
        viewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]

        binding.profileImage.setOnClickListener {
            imagePicker.launch("image/*")
        }
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()
            val phoneNo = binding.PhoneNo.text.toString()
            val rollNo = binding.etRollNo.text.toString()
            val pattern = Regex("\\d+@iiitu\\.ac\\.in$")

            if (!pattern.matches(email)) {
                Toast.makeText(requireContext(), "Enter a valid email like 12345@iiitu.ac.in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isNotEmpty() && password.isNotEmpty() && phoneNo.isNotEmpty() && name.isNotEmpty() && rollNo.isNotEmpty() && imageUri != null) {
                val imageBytes = uriToByteArray(requireContext(),imageUri!!)
                val compressBytes = compressImage(imageBytes!!)
                val base64String = Base64.encodeToString(compressBytes,Base64.DEFAULT)
                viewModel.loginUser(requireContext(),name, email, phoneNo, rollNo, password,base64String,Student_token)
            } else {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.authState.removeObservers(viewLifecycleOwner)
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(100) // Give NavController time to be ready
                        if (isAdded && view != null) {
                            try {
                                findNavController().navigate(R.id.action_studnetSignUpFragment_to_studentMainFragment)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(requireContext(), "Nav error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        return binding.root
    }
    private val  imagePicker= registerForActivityResult(ActivityResultContracts.GetContent()){uri->
        if(uri!= null){
            binding.profileImage.setImageURI(uri)
             imageUri = uri
        }
    }
    fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("FCM", "FCM Token: $token")
                Student_token = token
            }
    }
}
