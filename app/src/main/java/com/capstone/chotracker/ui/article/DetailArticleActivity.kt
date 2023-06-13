package com.capstone.chotracker.ui.article

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.capstone.chotracker.R
import com.capstone.chotracker.data.response.article.DataItem
import com.capstone.chotracker.databinding.ActivityDetailArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class DetailArticleActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val article = intent.getParcelableExtra<DataItem>(EXTRA_ARTICLE) as DataItem
        getDetailArticle(article)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_title, article.iD)

        binding.backtoListActicle.setOnClickListener {
            finish() // Menutup ArticleActivity dan kembali ke Fragment sebelumnya (HomeFragment)
        }

    }

    private fun getDetailArticle(article : DataItem) {
        binding.apply {
            tvDetailArticle.text = article.judulArticle
            descDetail.text = article.isiArticle
            author.text = article.author
            lastCreate.text = formatDate(article.createdAt)
        }
        Glide.with(this)
            .load(article.imageUrl)
            .into(binding.imageDetail)
    }
    private fun formatDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate!!)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ARTICLE = "extra_article"
    }
}