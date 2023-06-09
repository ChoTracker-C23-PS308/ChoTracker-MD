package com.capstone.chotracker.ui.history

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.chotracker.R
import com.capstone.chotracker.custom_view.CustomPopUpAlert
import com.capstone.chotracker.data.response.history.HistoryListResponseModel
import com.capstone.chotracker.databinding.ActivityHistoryBinding
import com.capstone.chotracker.ui.main.MainActivity
import com.capstone.chotracker.utils.ResultCondition
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryListAdapter
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolBar()

        historyViewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        auth = FirebaseAuth.getInstance()

        CoroutineScope(Dispatchers.Main).launch {
            val token = getFirebaseToken()
            val currentUser = auth.currentUser
            val uid = currentUser?.uid
            currentUser?.let {
                if (token != null) {
                    if (uid != null) {
                        historyViewModel.getHistories(
                            token, uid,
                        )
                    }
                }
            }
        }
        historyObserve()
        setupRecyclerView(binding.root.context)
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


    private fun setupToolBar() {
        val toolbar: MaterialToolbar = binding.toolbar
        setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }

    private fun setupRecyclerView(context: Context) {
        val rvHistory = binding.rvListHistory

        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvHistory.layoutManager = GridLayoutManager(context, 2)
        } else {
            rvHistory.layoutManager = LinearLayoutManager(context)
        }

        historyAdapter = HistoryListAdapter()
        rvHistory.adapter = historyAdapter
    }

    private fun historyObserve() {
        historyViewModel.resultCondition.observe(this) { resultCondition ->
            when (resultCondition) {
                is ResultCondition.LoadingState -> showLoading(true)
                is ResultCondition.SuccessState -> {
                    if (resultCondition.data.isEmpty()) {
                        showLoading(false)
                        historyEmpty()
                    } else {
                        showLoading(false)
                        showData(resultCondition.data)
                    }
                }
                is ResultCondition.ErrorState -> {
                    showLoading(false)
                    showError(resultCondition.data, true)
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        if (loading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvListHistory.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.rvListHistory.visibility = View.VISIBLE
        }
    }

    private fun showData(histories: List<HistoryListResponseModel>) {
        historyAdapter.submitList(histories)
    }

    private fun historyEmpty() {
       binding.historyEmpty.visibility = View.VISIBLE
       binding.rvListHistory.visibility = View.GONE
    }

    private fun showError(errorMessage: Int, error: Boolean) {
        if (error) {
            val alert = CustomPopUpAlert(this, errorMessage)
            alert.show()
            alert.setOnDismissListener {
                finish()
            }
        }
    }

}