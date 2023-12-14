package com.example.eden.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.viewmodels.SearchViewModel
import com.example.eden.viewmodels.SearchViewModelFactory
import com.example.eden.adapters.ViewPagerAdapter
import com.example.eden.databinding.ActivitySearchableBinding
import com.google.android.material.tabs.TabLayoutMediator

class SearchableActivity: AppCompatActivity() {
    private lateinit var searchableActivityBinding: ActivitySearchableBinding
    lateinit var searchQuery: String
    lateinit var viewModel: SearchViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchableActivityBinding = ActivitySearchableBinding.inflate(layoutInflater)
        setContentView(searchableActivityBinding.root)
        val application = application as Eden
        val factory = SearchViewModelFactory(application.repository, intent.getStringExtra(SearchManager.QUERY).toString(), application)
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]
        Log.v("Inside OnCreate", viewModel.toString())
        Log.v("Inside OnCreate", intent.getStringExtra(SearchManager.QUERY).toString())
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        viewModel.searchQuery = intent.getStringExtra(SearchManager.QUERY).toString()
        viewModel.refreshPostList()
        viewModel.refreshCommunityList()
        Log.i("Inside OnNewIntent", viewModel.toString())
        Log.i("Inside OnNewIntent", intent.getStringExtra(SearchManager.QUERY).toString())
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        searchableActivityBinding.searchView.clearFocus()
        searchableActivityBinding.searchView.setQuery(viewModel.searchQuery, false)

        searchableActivityBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchableActivityBinding.searchView.clearFocus()
                Intent(this@SearchableActivity, SearchableActivity:: class.java).apply {
                    putExtra(SearchManager.QUERY, query)
                    startActivity(this)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })


        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        searchableActivityBinding.viewPager2.adapter = viewPagerAdapter

        TabLayoutMediator(searchableActivityBinding.tabLayout, searchableActivityBinding.viewPager2){ tab, position ->
            when(position){
                0 -> tab.text = "Posts"
                1 -> tab.text = "Communities"
            }
        }.attach()
    }

}