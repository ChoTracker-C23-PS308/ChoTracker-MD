import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.capstone.chotracker.data.response.article.DataItem
import com.capstone.chotracker.databinding.FragmentHomeBinding
import com.capstone.chotracker.databinding.ItemListArticleBinding
import com.capstone.chotracker.ui.article.ArticleActivity
import com.capstone.chotracker.ui.article.DetailArticleActivity

import com.capstone.chotracker.ui.history.HistoryActivity
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var binding1: ItemListArticleBinding
    private val articleViewModel by viewModels<ArticleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding1 = ItemListArticleBinding.inflate(layoutInflater)

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvArticleAtHome.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvArticleAtHome.addItemDecoration(itemDecoration)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvArticleAtHome)

        articleViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        articleViewModel.message.observe(viewLifecycleOwner) {
            getArticle(articleViewModel.artcle)
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

        binding.seeAllHistory.setOnClickListener {
            seeAllHistory()
        }

        binding.seeArticle.setOnClickListener {
            seeAllArticle()
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
            binding.rvArticleAtHome.adapter = listArticleAdapter

            listArticleAdapter.setOnItemClickCallback(object : ArticleAdapter.OnItemClickCallback {
                override fun onItemClicked(data: DataItem) {
                    val intent = Intent(requireContext(), DetailArticleActivity::class.java)
                    intent.putExtra(DetailArticleActivity.EXTRA_ARTICLE, data)
                    startActivity(intent)
                }
            })
        }
    }

    private fun seeAllHistory() {
        val intent = Intent(requireContext(), HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun seeAllArticle() {
        val intent = Intent(requireContext(), ArticleActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
