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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.capstone.chotracker.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)

        logoutButtonHandler()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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