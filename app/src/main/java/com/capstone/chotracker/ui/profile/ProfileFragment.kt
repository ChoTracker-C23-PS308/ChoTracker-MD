package com.capstone.chotracker.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.capstone.chotracker.R
import com.capstone.chotracker.custom_view.CustomPopUpAlert
import com.capstone.chotracker.data.preferences.UserPreference
import com.capstone.chotracker.databinding.FragmentProfileBinding
import com.capstone.chotracker.ui.on_boarding.OnBoardingActivity
import com.capstone.chotracker.ui.profile.profile_detail.ProfileDetailActivity
import com.capstone.chotracker.ui.profile.profile_detail.ProfileDetailViewModel
import com.capstone.chotracker.utils.ResultCondition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var userPreference: UserPreference
    private val userViewModel by viewModels<ProfileDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreference.getInstance(requireContext())

        binding.profile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileDetailActivity::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            requireActivity(),
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )

        logoutButtonHandler()

        val tvLanguage: ConstraintLayout = binding.language
        tvLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))

        }

        getProfileUser()
        getProfileUserObserve()

    }

    private fun getProfileUser() {
        CoroutineScope(Dispatchers.Main).launch {
            val token = getFirebaseToken()
            val currentUser = auth.currentUser
            val uid = currentUser?.uid
            currentUser?.let {
                if (token != null) {
                    if (uid != null) {
                        userViewModel.getUserProfile(
                            token, uid,
                        )
                    }
                }
            }
        }

    }

    private fun getProfileUserObserve() {
        userViewModel.userProfileState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultCondition.LoadingState -> {
                    showLoading(true)
                }
                is ResultCondition.SuccessState -> {
                    showLoading(false)
                    val data = result.data
                    Log.d("ProfileDetailActivity", "Name: ${data.Name}, Email: ${data.Email}, BirthDate: ${data.BirthDate}, ImageUrl: ${data.ImageUrl}")

                    binding.nameProfile.setText(data.Name)
                    binding.emailProfile.setText(data.Email)

                    Glide.with(this)
                        .load(data.ImageUrl)
                        .error(R.drawable.image_profile) // Gambar default saat terjadi kesalahan
                        .fallback(R.drawable.image_profile) // Gambar default jika URL kosong atau null
                        .into(binding.photoProfile)

                }
                is ResultCondition.ErrorState -> {
                    showLoading(false)
                    CustomPopUpAlert(requireContext(), R.string.error_message).show()
                }
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.mainLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logoutButtonHandler() {
        binding.logout.setOnClickListener {
            userPreference.namePref = null
            userPreference.emailPref = null
            logOut()
        }
    }

    private fun logOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            val intent = Intent(requireContext(), OnBoardingActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
