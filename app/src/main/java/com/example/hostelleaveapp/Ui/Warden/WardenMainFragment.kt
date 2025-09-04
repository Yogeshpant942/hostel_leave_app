package com.example.hostelleaveapp.Ui.Warden

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.hostelleaveapp.LogInRepositary.wardenLogin.WardenLoginRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.ViewModel.WardenViewModel.signUpViewmodel
import com.example.hostelleaveapp.ViewModelFactory.Warden.SIgnUpFactory
import com.example.hostelleaveapp.databinding.FragmentWardenMainBinding

class WardenMainFragment : Fragment() {
    private lateinit var binding: FragmentWardenMainBinding
    private lateinit var viewModel: signUpViewmodel
    private val TAG = "WardenMainFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWardenMainBinding.inflate(inflater, container, false)
        val repo = WardenLoginRepo()
        val factory = SIgnUpFactory(repo)
        viewModel = ViewModelProvider(this, factory)[signUpViewmodel::class.java]

        viewModel.getWardenDetail()

        viewModel.Student_detail.observe(viewLifecycleOwner) { warden ->
            if (warden != null) {
                binding.wardenName.text = warden.name

                warden.image?.let { base64String ->
                    try {
                        Log.d(TAG, "Original Base64 length from Firestore: ${base64String.length}")
                        Log.d(TAG, "Original Base64 snippet: ${base64String.take(50)}...")

                        val cleanedBase64 = if (base64String.contains("base64,")) {
                            base64String.substringAfter("base64,").replace("\\s".toRegex(), "")
                        } else {
                            base64String.replace("\\s".toRegex(), "")
                        }

                        Log.d(TAG, "Cleaned Base64 length: ${cleanedBase64.length}")
                        if (cleanedBase64.isNotEmpty()) {
                            val decodedBytes = Base64.decode(cleanedBase64, Base64.DEFAULT)

                            Log.d(TAG, "Decoded bytes size: ${decodedBytes.size}")

                            if (decodedBytes.isNotEmpty()) {
                                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                bitmap?.let {
                                    binding.wardenProfile.setImageBitmap(it)
                                    Log.d(TAG, "Profile image loaded successfully.")
                                } ?: Log.e(TAG, "Bitmap decoding failed: null bitmap.")
                            } else {
                                Log.e(TAG, "Decoded bytes are empty. Base64 might be invalid.")
                            }
                        } else {
                            Log.e(TAG, "Cleaned Base64 string is empty.")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Base64 decode error: ${e.localizedMessage}")
                    }
                }
            } else {
                Log.d("WardenMainFragment", "Warden details are null")
            }
        }

        setupNavigation()

        binding.btnLogout.setOnClickListener {
            viewModel.logOutUser()
        }

        viewModel.logOutstatus.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut == true) {
                findNavController().popBackStack()
            }
        }
        
        binding.cardViewChat.setOnClickListener {
         findNavController().navigate(R.id.action_wardenMainFragment_to_chatHomeFragment)
        }
        return binding.root
    }

    private fun setupNavigation() {
        binding.cardViewApplications.setOnClickListener {
            navigateToLeaveRequests("all")
        }
        binding.accepted.setOnClickListener {
            navigateToLeaveRequests("accepted")
        }
        binding.rejected.setOnClickListener {
            navigateToLeaveRequests("rejected")
        }
        binding.pending.setOnClickListener {
            navigateToLeaveRequests("pending")
        }
    }

    private fun navigateToLeaveRequests(filter: String) {
        val bundle = Bundle().apply {
            putString("filter", filter)
        }
        findNavController().navigate(R.id.action_wardenMainFragment_to_leaveRequestFragment, bundle)
    }
}
