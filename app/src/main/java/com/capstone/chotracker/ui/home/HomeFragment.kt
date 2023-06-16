import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.capstone.chotracker.data.preferences.UserPreference
import com.capstone.chotracker.data.response.article.DataItem
import com.capstone.chotracker.databinding.FragmentHomeBinding
import com.capstone.chotracker.databinding.ItemListArticleBinding
import com.capstone.chotracker.ui.article.ArticleActivity
import com.capstone.chotracker.ui.article.DetailArticleActivity
import com.capstone.chotracker.ui.chochat.ads.AdsActivity

import com.capstone.chotracker.ui.history.HistoryActivity
import com.capstone.chotracker.ui.profile.profile_detail.ProfileDetailViewModel
import com.capstone.chotracker.utils.ResultCondition
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var binding1: ItemListArticleBinding
    private val articleViewModel by viewModels<ArticleViewModel>()
    private val userViewModel by viewModels<ProfileDetailViewModel>()
    private lateinit var userPreference: UserPreference
    private lateinit var auth: FirebaseAuth

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
        userPreference = UserPreference.getInstance(requireContext())

        auth = FirebaseAuth.getInstance()

        CoroutineScope(Dispatchers.Main).launch {
            val token = getFirebaseToken()
            val currentUser = auth.currentUser
            val uid = currentUser?.uid
            currentUser?.let {
                if (token != null) {
                    if (uid != null) {
                        articleViewModel.getArticle(token)
                        userViewModel.getUserProfile(token, uid)
                    }
                }
            }
        }

        getNameUserObserve()
        getArticleObserve()

        seeAllHistory()
        seeAllArticle()
        navigateToAds()

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

    private fun getArticleObserve() {
        articleViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        articleViewModel.message.observe(viewLifecycleOwner) {
            getArticle(articleViewModel.artcle)
        }
    }

    private fun getNameUserObserve() {
        userViewModel.userProfileState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultCondition.LoadingState -> {
                    showLoading(true)
                }
                is ResultCondition.SuccessState -> {
                    showLoading(false)
                    val data = result.data

                    val name = "Hi, ${data.Name}"
                    binding.tvNameHello.setText(name)

                }
                is ResultCondition.ErrorState -> {
                    showLoading(false)
                    if (userPreference.namePref != null) {
                        val name = "Hi, ${userPreference.namePref}"
                        binding.tvNameHello.setText(name)
                    }
                }
            }
        }

    }

    private suspend fun getFirebaseToken(): String? {
        val currentUser = auth.currentUser
        return try {
            currentUser?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            Log.e("Firebase Token", "Error getting Firebase token: ${e.message}")
            null
        }
    }

    private fun seeAllHistory() {
        binding.seeAllHistory.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun seeAllArticle() {
        binding.seeArticle.setOnClickListener {
            val intent = Intent(requireContext(), ArticleActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToAds() {
        binding.vfBanner.setOnClickListener {
            val intent = Intent(requireContext(), AdsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.mainLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showNoData(isNoData: Boolean) {
        binding.noData.visibility = if (isNoData) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
