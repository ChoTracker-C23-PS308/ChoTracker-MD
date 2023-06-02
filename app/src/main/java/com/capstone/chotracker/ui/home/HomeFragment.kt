package com.capstone.chotracker.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.chotracker.R
import com.capstone.chotracker.databinding.FragmentHomeBinding
import com.capstone.chotracker.ui.article.ArticleFragment
import com.capstone.chotracker.ui.profile.DetailProfileFragment


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding ? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.seeArticle.setOnClickListener {
            val categoryFragment = ArticleFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().apply {
                replace(
                    R.id.container_home,
                    categoryFragment,
                    ArticleFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}