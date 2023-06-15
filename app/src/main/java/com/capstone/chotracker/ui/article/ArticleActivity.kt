package com.capstone.chotracker.ui.article

import ArticleAdapter
import ArticleViewModel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.chotracker.data.response.article.DataItem
import com.capstone.chotracker.databinding.ActivityArticleBinding
import com.capstone.chotracker.databinding.ItemListArticleBinding
import com.google.firebase.auth.FirebaseAuth

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding
    private lateinit var binding1: ItemListArticleBinding
    private val articleViewModel by viewModels<ArticleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding1 = ItemListArticleBinding.inflate(layoutInflater)

        val layoutManager = LinearLayoutManager(this)
        binding.rvArticle.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvArticle.addItemDecoration(itemDecoration)

        articleViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        articleViewModel.message.observe(this) {
            getArticle(articleViewModel.artcle)
        }

        binding.backArticle.setOnClickListener {
            finish() // Menutup ArticleActivity dan kembali ke Fragment sebelumnya (HomeFragment)
        }


        val user = FirebaseAuth.getInstance().currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result?.token
                if (token != null) {
                    articleViewModel.getArticle(token)
                } else {
                    // Gagal mendapatkan token
                }
            } else {
                // Gagal mendapatkan token
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showNoData(isNoData: Boolean) {
        binding.noData.visibility = if (isNoData) View.VISIBLE else View.GONE
    }

    private fun getArticle(article: List<DataItem>) {
        if (article.isEmpty()) {
            showNoData(true)
        } else {
            showNoData(false)
            val listArticleAdapter = ArticleAdapter(article)
            binding.rvArticle.adapter = listArticleAdapter

            listArticleAdapter.setOnItemClickCallback(object : ArticleAdapter.OnItemClickCallback{
                override fun onItemClicked(data: DataItem) {

                    val intent = Intent(this@ArticleActivity, DetailArticleActivity::class.java)
                    intent.putExtra(DetailArticleActivity.EXTRA_ARTICLE, data)
                    startActivity(intent)
                }
            })
        }
    }


}
