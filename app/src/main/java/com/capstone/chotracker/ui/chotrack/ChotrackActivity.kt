package com.capstone.chotracker.ui.chotrack

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.capstone.chotracker.R
import com.capstone.chotracker.custom_view.CustomPopUpAlert
import com.capstone.chotracker.databinding.ActivityChotrackBinding
import com.capstone.chotracker.ui.history.HistoryActivity
import com.capstone.chotracker.ui.main.MainActivity
import com.capstone.chotracker.utils.ResultCondition
import com.capstone.chotracker.utils.getFileFromUri
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ChotrackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChotrackBinding
    private lateinit var imageUri: Uri
    private lateinit var viewModel: ChotrackViewModel
    private lateinit var auth: FirebaseAuth

    private var predictionResult: Float = 10.0f
    private var levelResult: String = "Normal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChotrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra(MainActivity.EXTRA_IMAGE)
        if (imagePath != null) {
            imageUri = Uri.parse(imagePath)
            binding.ivResult.setImageURI(imageUri)
        }

        viewModel = ViewModelProvider(this).get(ChotrackViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        buttonPredictHandler()
        predictObserve()

        saveResultHandler()
        saveResultObserve()

    }

    private fun buttonPredictHandler() {
        binding.resultButton.setOnClickListener {
            val imageFile = getFileFromUri(this, imageUri)
            val requestFile = imageFile?.asRequestBody("multipart/form-data".toMediaType())
            val imagePart = requestFile?.let {
                MultipartBody.Part.createFormData("file", imageFile.name, it)
            }

            if (imagePart != null) {
                viewModel.predictCholesterol(imagePart)
            }
        }
    }

    private fun predictObserve() {
        viewModel.predictState.observe(this) { result ->
            when (result) {
                is ResultCondition.LoadingState -> {
                    progressLoading(true)
                }
                is ResultCondition.SuccessState -> {
                    progressLoading(false)
                    handleSuccess(result.data)
                }
                is ResultCondition.ErrorState -> {
                    progressLoading(false)
                    handleError(result.data)
                }
            }
        }
    }

    private fun handleSuccess(prediction: Float?) {
        prediction?.let {
            binding.tvTotalCholesterol.text = it.toString()

            val level: String = when {
                it in 0.0..199.0 -> {
                    binding.tvCholesterolLevel.text = "Normal"
                    binding.tvCholesterolLevel.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                    "Normal"
                }
                it in 200.0..239.0 -> {
                    binding.tvCholesterolLevel.text = "At Risk"
                    binding.tvCholesterolLevel.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                    "At Risk"
                }
                it > 240 -> {
                    binding.tvCholesterolLevel.text = "High"
                    binding.tvCholesterolLevel.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                    "High"
                }
                else -> {
                    binding.tvCholesterolLevel.text = "Unidentified"
                    binding.tvCholesterolLevel.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
                    "Unidentified"
                }
            }

            predictionResult = it
            levelResult = level
        }
    }

    private fun saveResultHandler() {
        binding.saveResultButton.setOnClickListener {
            val imageFile = getFileFromUri(this, imageUri)
            val requestFile = imageFile?.asRequestBody("multipart/form-data".toMediaType())
            val imagePart = requestFile?.let {
                MultipartBody.Part.createFormData("file", imageFile.name, it)
            }

            if (imagePart != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val token = getFirebaseToken()
                    val uid = auth.currentUser?.uid
                    val totalKolesterol = predictionResult
                    val tingkat = levelResult

                    if (uid != null) {
                        if (token != null) {
                            viewModel.saveResult(token, uid, imagePart, totalKolesterol, tingkat)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getFirebaseToken(): String? {
        val currentUser = auth.currentUser
        return try {
            currentUser?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            Log.e("Firebase Token", "Error getting Firebase token: ${e.message}")
            null
        }
    }

    private fun saveResultObserve() {
        viewModel.addHistoryState.observe(this) { result ->
            when (result) {
                is ResultCondition.LoadingState -> {
                    progressSaveLoading(true)
                }
                is ResultCondition.SuccessState -> {
                    handleSaveResult(true)
                }
                is ResultCondition.ErrorState -> {
                    handleError(result.data)
                }
            }
        }
    }




    private fun handleSaveResult(success: Boolean) {
        if (success) {
            val alert = CustomPopUpAlert(this, R.string.save_result_success)
            alert.show()
            alert.setOnDismissListener {
                navigateToHistory()
            }
        }

    }

    private fun navigateToHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleError(errorMessage: Int) {
        CustomPopUpAlert(this, errorMessage).show()
    }

    private fun progressLoading(loading: Boolean) {
        binding.progressBar1.visibility = if (loading) View.VISIBLE else View.GONE
        binding.progressBar2.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun progressSaveLoading(loading: Boolean) {
        binding.progressBarSave.visibility = if (loading) View.VISIBLE else View.GONE
    }


}

