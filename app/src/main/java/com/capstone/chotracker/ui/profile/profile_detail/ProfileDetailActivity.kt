package com.capstone.chotracker.ui.profile.profile_detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.capstone.chotracker.R
import com.capstone.chotracker.chotrack_cam.ChotrackCamActivity
import com.capstone.chotracker.chotrack_cam.ChotrackCamOptions
import com.capstone.chotracker.custom_view.CustomPopUpAlert
import com.capstone.chotracker.data.response.profile.UserModel
import com.capstone.chotracker.databinding.ActivityProfileDetailBinding
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

class ProfileDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: ProfileDetailViewModel
    private lateinit var mChotrackCamOptions: ChotrackCamOptions
    private var selectedImagePath: String? = null
    private var imageUri: Uri? = null
    private var userModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this)[ProfileDetailViewModel::class.java]

        mChotrackCamOptions = ChotrackCamOptions()

        getDataUserProfile()
        getDataUserProfileObserve()
        changePictureButtonHandler()
        lastEditStop()
        updateUserProfile()
        updateUserProfileObserve()

    }

    private fun getDataUserProfile() {
        CoroutineScope(Dispatchers.Main).launch {
            val uid = auth.currentUser?.uid
            val token = getFirebaseToken()

            token?.let {
                uid?.let { it1 ->
                    viewModel.getUserProfile(
                        token = it,
                        id = it1
                    )
                }
            }
        }
    }

    private fun getDataUserProfileObserve() {
        viewModel.userProfileState.observe(this) { result ->
            when (result) {
                is ResultCondition.LoadingState -> {
                    showLoading(true)
                }
                is ResultCondition.SuccessState -> {
                    showLoading(false)
                    val data = result.data

                    Log.d("ProfileDetailActivity", "Name: ${data.Name}, Email: ${data.Email}, BirthDate: ${data.BirthDate}, ImageUrl: ${data.ImageUrl}")

                    binding.nameEdit.setText(data.Name)
                    binding.emailEdit.setText(data.Email)
                    binding.birthDateEdit.setText(data.BirthDate)

                    Glide.with(this)
                        .load(data.ImageUrl)
                        .error(R.drawable.image_profile) // Gambar default saat terjadi kesalahan
                        .fallback(R.drawable.image_profile) // Gambar default jika URL kosong atau null
                        .into(binding.ivProfilePicture)


                    binding.rdChoiceMale.isChecked = data.Gender == "Male"
                    binding.rdChoiceFemale.isChecked = data.Gender != "Male"

                }
                is ResultCondition.ErrorState -> {
                    showLoading(false)
                    showErrorLoadProfile()
                }
            }
        }
    }

    private fun changePictureButtonHandler() {
        binding.buttonChangePicture.setOnClickListener {
            ChotrackCamActivity.startPicker(this, mChotrackCamOptions)
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == ChotrackCamActivity.REQUEST_CODE_PICKER){
            val mImageList = data?.getStringArrayListExtra(ChotrackCamActivity.PICKED_MEDIA_LIST) as ArrayList<String>
            selectedImagePath = mImageList[0]
            imageUri = Uri.parse(selectedImagePath)
            binding.ivProfilePicture.setImageURI(imageUri)
            updateProfilePicture(imageUri)
        }
    }

    private fun updateProfilePicture(imageUri: Uri?) {
        CoroutineScope(Dispatchers.Main).launch {
            val uid = auth.currentUser?.uid
            val token = getFirebaseToken()

            val imageFile = imageUri?.let { getFileFromUri(this@ProfileDetailActivity, it) }
            val requestFile = imageFile?.asRequestBody("multipart/form-data".toMediaType())
            val imagePart = requestFile?.let {
                MultipartBody.Part.createFormData("file", imageFile.name, it)
            }

            if (imagePart != null) {
                token?.let {
                    uid?.let { it1 ->
                        viewModel.updateImageProfile(
                            token = it,
                            uid = it1,
                            imagePart = imagePart
                        )
                    }
                }
            }
        }
    }

    private fun updateUserProfile() {
        binding.buttonUpdate.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val uid = auth.currentUser?.uid
                val token = getFirebaseToken()

                val name = binding.nameEdit.text.toString()
                val email = binding.emailEdit.text.toString()
                val birthDate = binding.birthDateEdit.text.toString()

                val selectedOptionId = binding.rdGender.checkedRadioButtonId
                if (selectedOptionId != -1) {
                    val selectedRadioButton = findViewById<RadioButton>(selectedOptionId)
                    val gender = selectedRadioButton.text.toString()

                    token?.let { it1 ->
                        uid?.let { it2 ->
                            viewModel.updateUserProfile(
                                token = it1,
                                id = it2,
                                name = name,
                                email = email,
                                birth_date = birthDate,
                                gender = gender
                            )
                        }
                    }

                } else {
                    CustomPopUpAlert(this@ProfileDetailActivity, R.string.make_sure_all_data).show()
                }

            }

        }
    }

    private fun updateUserProfileObserve() {
       viewModel.updateImageState.observe(this) { resultCondition ->
           when (resultCondition) {
               is ResultCondition.LoadingState -> {}
               is ResultCondition.SuccessState -> {
                   showLoading(false)
                   viewModel.updateUserState.observe(this) { result ->
                       when (result) {
                           is ResultCondition.LoadingState -> showLoading(true)
                           is ResultCondition.SuccessState -> {
                               showLoading(false)
                               updateSuccess()
                           }
                           is ResultCondition.ErrorState -> {
                               showLoading(false)
                               showError()
                           }
                       }
                   }

               }
               is ResultCondition.ErrorState -> {
                   showLoading(false)
                   showError()
               }
           }
       }
    }

    private fun showErrorLoadProfile() {
        val alert = CustomPopUpAlert(this, R.string.failed_load_profile)
        alert.show()
        alert.setOnDismissListener {
            finish()
        }
    }

    private fun showError() {
        CustomPopUpAlert(this, R.string.error_message).show()
    }


    private fun showLoading(loading: Boolean) {
       if (loading) {
           binding.progressBar.visibility = View.VISIBLE
           binding.detailProfileLayout.visibility = View.GONE
       } else {
           binding.progressBar.visibility = View.GONE
           binding.detailProfileLayout.visibility = View.VISIBLE
       }
    }


    private fun updateSuccess() {
        CustomPopUpAlert(this, R.string.update_profile_success).show()
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

    private fun lastEditStop() {
        binding.birthDateEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.birthDateEdit.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.birthDateEdit.windowToken, 0)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

}