package com.capstone.chotracker.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)

        logoutButtonHandler()

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