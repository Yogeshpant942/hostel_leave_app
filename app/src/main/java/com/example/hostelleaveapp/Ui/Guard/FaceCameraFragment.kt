package com.example.hostelleaveapp.Ui.Guard

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.DataModel.CompareResponse
import com.example.hostelleaveapp.FCM.Retrofit
import com.example.hostelleaveapp.ViewModel.GuardLeaveViewModel
import com.example.hostelleaveapp.ViewModelFactory.StudentDetailFactory
import com.example.hostelleaveapp.MainRepositary.GuardRepo.GuardMainDataRepo
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.RetrofitClient
import com.example.hostelleaveapp.databinding.FragmentFaceCameraBinding
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.File
import java.util.concurrent.Executors

class FaceCameraFragment : Fragment() {

    private lateinit var binding: FragmentFaceCameraBinding
    private lateinit var imageCapture: ImageCapture
    private lateinit var viewModel: GuardLeaveViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFaceCameraBinding.inflate(inflater, container, false)

        val repo = GuardMainDataRepo()
        val factory = StudentDetailFactory(repo)
        viewModel = ViewModelProvider(this, factory)[GuardLeaveViewModel::class.java]

        startCamera()

        binding.captureBtn.setOnClickListener {
            takePhoto()
        }

        return binding.root
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(requireActivity().windowManager.defaultDisplay.rotation)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setBufferFormat(ImageFormat.JPEG)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val photoFile = File(requireContext().cacheDir, "captured_face.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(requireContext(), "Photo Captured", Toast.LENGTH_SHORT).show()

                    val capturedImageBase64 = encodeImageToBase64(photoFile)
                    val studentImageBase64 = arguments?.getString("image_base64")

                    if (studentImageBase64.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "Student image not found!", Toast.LENGTH_SHORT).show()
                        return
                    }

                    compareFaces(studentImageBase64, capturedImageBase64)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun encodeImageToBase64(file: File): String {
        val bytes = file.readBytes()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun compareFaces(image1Base64: String, image2Base64: String) {
        val apiKey = "Sruw0XcFl9JQh1biOSznVjYcD-W799wc"
        val apiSecret = "1eswBuqk0RZkNEv8xWDLSE_j9Llykcv9"


        RetrofitClient.instance.compareFaces(apiKey, apiSecret, image1Base64, image2Base64)
            .enqueue(object : Callback<CompareResponse> {
                override fun onResponse(
                    call: Call<CompareResponse>,
                    response: Response<CompareResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val confidence = result?.confidence ?: 0f
                        val threshold = result?.thresholds?.get("1e-5") ?: 0f
                        val matched = confidence >= threshold
                        val message = if (matched) {
                            "Match! Confidence: $confidence"
                        } else {
                            "Not a Match. Confidence: $confidence"
                        }
                        val rollNO = arguments!!.getString("rollNo")

                        val bundle = Bundle().apply {
                               putString("match_confidence",confidence.toString())
                            putString("rollNo",rollNO)
                        }
                        findNavController().navigate(R.id.action_faceCameraFragment_to_studentDetailFragment,bundle)
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                        Log.d("FaceMatch", message)
                    } else {
                        Toast.makeText(requireContext(), "Face comparison failed.", Toast.LENGTH_SHORT).show()
                        Log.e("FaceMatch", "Error: ${response.errorBody()?.string()}")
                        Log.e("FaceMatch", "Failure: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<CompareResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "API Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("FaceMatch", "Failure: ${t.message}")
                }
            })
    }


}
