package com.capstone.chotracker.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import com.capstone.chotracker.databinding.FragmentProfileBinding
import com.capstone.chotracker.ui.on_boarding.OnBoardingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

=======
import androidx.fragment.app.FragmentTransaction
import com.capstone.chotracker.R
import com.capstone.chotracker.databinding.FragmentDetailProfileBinding
import com.capstone.chotracker.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
        private var _binding: FragmentProfileBinding? = null
        private val binding get() = _binding!!
>>>>>>> 4cbdf26be7e2da82cb5f0b739a93fab9d32c25c5
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
<<<<<<< HEAD
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
=======
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater)
>>>>>>> 4cbdf26be7e2da82cb5f0b739a93fab9d32c25c5
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

<<<<<<< HEAD
        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)

        logoutButtonHandler()

=======
        binding.editProfile.setOnClickListener {
            val categoryFragment = DetailProfileFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().apply {
                replace(
                    R.id.container,
                    categoryFragment,
                    DetailProfileFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
>>>>>>> 4cbdf26be7e2da82cb5f0b739a93fab9d32c25c5
    }


    private fun logoutButtonHandler() {
        binding.logout.setOnClickListener {
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