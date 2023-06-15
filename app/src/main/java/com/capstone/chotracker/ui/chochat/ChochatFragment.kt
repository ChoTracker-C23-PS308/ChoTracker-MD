package com.capstone.chotracker.ui.chochat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.chotracker.databinding.FragmentChochatBinding
import com.capstone.chotracker.ui.chochat.mycho_bot.MychoBotActivity

class ChochatFragment : Fragment() {

    private var _binding: FragmentChochatBinding? = null
    private val binding get() = _binding!!

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
        openMychobotChat()
    }

    fun openMychobotChat() {
        binding.mychoBot.setOnClickListener {
            val intent = Intent(requireContext(), MychoBotActivity::class.java)
            startActivity(intent)
        }
    }


}