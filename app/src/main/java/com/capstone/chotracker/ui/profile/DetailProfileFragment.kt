package com.capstone.chotracker.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.chotracker.R
import com.capstone.chotracker.databinding.FragmentDetailProfileBinding


class DetailProfileFragment : Fragment() {

    private var _binding: FragmentDetailProfileBinding ? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backFromEditProfile.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}