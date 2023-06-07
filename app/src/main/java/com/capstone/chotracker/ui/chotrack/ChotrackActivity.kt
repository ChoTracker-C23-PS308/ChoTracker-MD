package com.capstone.chotracker.ui.chotrack

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capstone.chotracker.R
import com.capstone.chotracker.custom_view.CustomPopUpAlert
import com.capstone.chotracker.databinding.ActivityChotrackBinding
import com.capstone.chotracker.ui.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ChotrackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChotrackBinding
    private lateinit var imageUri: Uri
    private lateinit var viewModel: ChotrackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChotrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra(MainActivity.EXTRA_IMAGE)
        if (imagePath != null) {
            imageUri = Uri.parse(imagePath)
            binding.ivResult.setImageURI(imageUri)
        }

        viewModel = ViewModelProvider(this)[ChotrackViewModel::class.java]

        viewModel.loadingState.observe(this, Observer { isLoading ->
            progressLoading(isLoading)
        })

        viewModel.successState.observe(this, Observer { prediction ->
            handleSuccess(prediction)
        })

        viewModel.errorState.observe(this, Observer { errorMessage ->
            handleError(errorMessage)
        })

        binding.resultButton.setOnClickListener {
            buttonPredictHandler()
        }
    }

    private fun buttonPredictHandler() {
        val imageFile = getFileFromUri(imageUri)
        val requestFile = imageFile?.asRequestBody("multipart/form-data".toMediaType())
        val imagePart = requestFile?.let {
            MultipartBody.Part.createFormData("file", imageFile.name, it)
        }

        if (imagePart != null) {
            viewModel.predictCholesterol(imagePart)
        }
    }

    private fun handleSuccess(prediction: Float?) {
        prediction?.let {
            binding.tvTotalCholesterol.text = it.toString()

            if (prediction in 0.0..199.0) {
                binding.tvCholesterolLevel.setText("Normal")

                val greenColor = ContextCompat.getColor(this, R.color.green)
                binding.tvCholesterolLevel.setBackgroundColor(greenColor)
            } else if (prediction in 200.0..239.0) {
                binding.tvCholesterolLevel.setText("At Risk")

                val colorOrange = ContextCompat.getColor(this, R.color.orange)
                binding.tvCholesterolLevel.setBackgroundColor(colorOrange)
            } else if (prediction > 240) {
                binding.tvCholesterolLevel.setText("High")

                val colorRed = ContextCompat.getColor(this, R.color.red)
                binding.tvCholesterolLevel.setBackgroundColor(colorRed)
            } else {
                binding.tvCholesterolLevel.setText("Unidentified")

                val colorBlack = ContextCompat.getColor(this, R.color.black)
                binding.tvCholesterolLevel.setBackgroundColor(colorBlack)
            }
        }
    }

    private fun handleError(errorMessage: Int?) {
        errorMessage?.let {
            CustomPopUpAlert(this, it).show()
        }
    }

    private fun progressLoading(loading: Boolean) {
        if (loading) {
            binding.progressBar1.visibility = View.VISIBLE
            binding.progressBar2.visibility = View.VISIBLE
        } else {
            binding.progressBar1.visibility = View.GONE
            binding.progressBar2.visibility = View.GONE
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File.createTempFile("temp_image", null, cacheDir)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}