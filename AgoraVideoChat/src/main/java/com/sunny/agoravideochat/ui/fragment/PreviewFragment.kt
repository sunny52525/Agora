package com.sunny.agoravideochat.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sunny.agoravideochat.R

import com.sunny.agoravideochat.viewmodel.VideoChatViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class PreviewFragment : Fragment(R.layout.fragment_preview) {

    private lateinit var viewModel: VideoChatViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(VideoChatViewModel::class.java)

        viewModel.cameraPermissionGranted.observe(viewLifecycleOwner) {
            setupLocalVideo()
        }


        viewModel.micPermissionGranted.observe(viewLifecycleOwner) {
            viewModel.micOn.postValue(it)
        }
//        binding.cameraModeToggle.setOnClickListener {
//            viewModel.toggleFrontCamera()
//        }
//
//        binding.joinMeetingButton.setOnClickListener {
//            //open Fragment
//            openFragment(VideoChatFragment())
//        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_view, fragment)
        transaction.commit()
    }

    private fun setupLocalVideo() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//
//        cameraProviderFuture.addListener({
//
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//            // Preview
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.localVideoContainer.surfaceProvider)
//                }
//
//            var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
//
//            viewModel.frontCamera.observe(viewLifecycleOwner) {
//                cameraSelector = if (it) {
//                    CameraSelector.DEFAULT_FRONT_CAMERA
//                } else {
//                    CameraSelector.DEFAULT_BACK_CAMERA
//                }
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
//
//            }
//            viewModel.cameraOn.observe(viewLifecycleOwner) {
//                if (it) {
//                    cameraProvider.unbindAll()
//                    cameraProvider.bindToLifecycle(this, cameraSelector, preview)
//                    binding.localVideoContainer.isVisible = true
//
//
//                } else {
//                    cameraProvider.unbindAll()
//                    binding.localVideoContainer.isVisible = false
//                }
//            }
//
//            try {
//
//                cameraProvider.unbindAll()
//
//                cameraProvider.bindToLifecycle(
//                    this, cameraSelector, preview)
//
//            } catch (exc: Exception) {
//                Log.e(TAG, "Use case binding failed", exc)
//            }
//
//        }, ContextCompat.getMainExecutor(requireContext()))

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val TAG = "PreviewFragment"
    }
}