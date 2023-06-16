package com.capstone.chotracker.ui.chochat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.chotracker.data.preferences.UserPreference
import com.capstone.chotracker.databinding.FragmentChochatBinding
import com.capstone.chotracker.ui.chochat.ads.AdsActivity
import com.capstone.chotracker.ui.chochat.choose_doctor.ChooseDoctorActivity
import com.capstone.chotracker.ui.chochat.mycho_bot.MychoBotActivity

class ChochatFragment : Fragment() {

    private var _binding: FragmentChochatBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChochatBinding.inflate(layoutInflater)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreference.getInstance(requireContext())

        openMychobotChat()
        navigateToAds()
        checkSubscribe()
        chooseDoctor()
    }

    private fun navigateToAds() {
        binding.cvAds.setOnClickListener {
            val intent = Intent(requireContext(), AdsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openMychobotChat() {
        binding.mychoBot.setOnClickListener {
            val intent = Intent(requireContext(), MychoBotActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkSubscribe() {
        val check = userPreference.isSubscribePref

        binding.buttonFindDoctor.visibility = if (check) View.VISIBLE else View.GONE
        binding.layoutBlurAds.visibility = if (check) View.GONE else View.VISIBLE
    }

    private fun chooseDoctor() {
        binding.buttonFindDoctor.setOnClickListener {
            val intent = Intent(requireContext(), ChooseDoctorActivity::class.java)
            startActivity(intent)
        }
    }

}