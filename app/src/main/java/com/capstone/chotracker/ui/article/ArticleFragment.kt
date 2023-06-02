package com.capstone.chotracker.ui.article

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.chotracker.R
import com.capstone.chotracker.databinding.FragmentArticleBinding


class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding ? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentArticleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backFromArticle.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.article2.setOnClickListener {
            val categoryFragment = DetailArticleFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().apply {

                replace(
                    R.id.container_article,
                    categoryFragment,
                    DetailArticleFragment::class.java.simpleName
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