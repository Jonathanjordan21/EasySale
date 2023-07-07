package com.example.vendual.fragments

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.lang.StringBuilder
import androidx.activity.result.ActivityResultCallback

import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.example.vendual.*


class Ocr : Fragment(R.layout.fragment_ocr) {
    private lateinit var btnCapture : Button
    private lateinit var tvTextData : TextView
    private lateinit var bitmap : Bitmap
    private val REQUEST_IMAGE_CAPTURE = 1
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE_IMAGE = 1001


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCapture = requireView().findViewById(R.id.btnCapture)
        tvTextData = requireView().findViewById(R.id.tvTextData)


        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_CODE)
        }

        btnCapture.setOnClickListener {
            //CropImage.activity().start(requireContext(), this)
            if(checkPermissions()){
                //captureImage()
                pickImageGallery()

            } else reqPermission()
        }

//        val activityResultLauncher = registerForActivityResult(StartActivityForResult(),
//            ActivityResultCallback<ActivityResult> { result ->
//                if(result.resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//                    Log.d("IF1", "TRUE")
//                    val results = CropImage.getActivityResult(result.data)
//                    if(result.resultCode == RESULT_OK){
//                        Log.d("IF2", "TRUE")
//                        val uri = results.uri
//                        bitmap = if(Build.VERSION.SDK_INT < 28) {
//                            MediaStore.Images.Media
//                                .getBitmap(requireActivity().contentResolver, uri)
//                        } else {
//                            val source = ImageDecoder
//                                .createSource(requireActivity().contentResolver, uri)
//                            ImageDecoder.decodeBitmap(source)
//                        }
//                        getTextFromImage(bitmap)
//                    }
//                }
//            })

    }

    private fun checkPermissions():Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(requireActivity().applicationContext, CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(requireActivity().applicationContext, READ_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun reqPermission(){
        val PERMISSION_CODE = 200
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(CAMERA, READ_EXTERNAL_STORAGE), PERMISSION_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("ACTIVITY ON RESULT", "TRUE")
        Log.d("ACTIVITY ON RESULT", "$resultCode")
        Log.d("ACTIVITY ON RESULT", "$requestCode")

//        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//            Log.d("IF1", "TRUE")
//            val result = CropImage.getActivityResult(data)
//            if(resultCode == RESULT_OK){
//                Log.d("IF2", "TRUE")
//                val uri = result.uri
//                bitmap = if(Build.VERSION.SDK_INT < 28) {
//                    MediaStore.Images.Media
//                        .getBitmap(requireActivity().contentResolver, uri)
//                } else {
//                    val source = ImageDecoder
//                        .createSource(requireActivity().contentResolver, uri)
//                    ImageDecoder.decodeBitmap(source)
//                }
//                getTextFromImage(bitmap)
//            }
        if(requestCode == RESULT_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val extras = data?.extras
            bitmap = extras!!.get("data") as Bitmap
            getTextFromImage(bitmap)
        } else if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK){
            val imageUri = data!!.data
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
            getTextFromImage(bitmap)
        }
    }

    private fun captureImage(){
        Log.d("Capture ", "image")
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(takePicture.resolveActivity(requireActivity().packageManager)!= null){
            Log.d("success! ", " capturing image")
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE)
        }
    }


    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)

    }


    private fun getTextFromImage(bitmap : Bitmap){
        val recognizer = TextRecognizer.Builder(requireContext()).build()
        if(!recognizer.isOperational){
            makeToast(requireContext(), "Error Occured!")
        } else {
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val tsArray : SparseArray<TextBlock> =  recognizer.detect(frame)
            val str = StringBuilder()
            for(i in 0 until tsArray.size()){
                val tb = tsArray.valueAt(i)
                str.append(tb.value)
                str.append("\n")
            }
            tvTextData.text = str.toString()
        }
    }


}