package com.example.hostelleaveapp.Ui.Guard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.hostelleaveapp.MainRepositary.GuardRepo.GuardMainDataRepo
import com.example.hostelleaveapp.ViewModel.GuardLeaveViewModel
import com.example.hostelleaveapp.ViewModelFactory.StudentDetailFactory
import com.example.hostelleaveapp.databinding.FragmentStudentBarCodeInBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StudentBarCodeInFragment : Fragment() {
    private var _binding: FragmentStudentBarCodeInBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: GuardLeaveViewModel

    private lateinit var cameraExecutor: ExecutorService

    companion object {
        private const val CAMERA_PERMISSION_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentBarCodeInBinding.inflate(inflater, container, false)
        val repo = GuardMainDataRepo()
        val factory = StudentDetailFactory(repo)
        viewModel = ViewModelProvider(this,factory)[GuardLeaveViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cameraExecutor = Executors.newSingleThreadExecutor()

        viewModel.checkExistOrNOt.observe(viewLifecycleOwner){exist->
            if(exist){
                Toast.makeText(requireContext(),"Successfully Checked In",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(), "You never checked out. Please check out once to check in.", Toast.LENGTH_SHORT).show()
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val barcodeScanner = BarcodeScanning.getClient()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(barcodeScanner, imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("BarCodeFragment", "Camera use case binding failed", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(scanner: com.google.mlkit.vision.barcode.BarcodeScanner, imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        rawValue?.let {
                            Log.d("BarcodeResult", "Scanned: $it")
                            checkLeaveExistOrNot(it)
                            Toast.makeText(requireContext(),it,Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("BarcodeResult", "Scan failed: ${it.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun checkLeaveExistOrNot(rollNo: String) {
        viewModel.fetchStudentExist(rollNo)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }
}
