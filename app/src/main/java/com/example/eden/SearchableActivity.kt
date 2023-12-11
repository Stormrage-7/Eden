package com.example.eden

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eden.databinding.ActivitySearchableBinding

class SearchableActivity: AppCompatActivity() {
    private lateinit var searchableActivityBinding: ActivitySearchableBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchableActivityBinding = ActivitySearchableBinding.inflate(layoutInflater)
        setContentView(searchableActivityBinding.root)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.getStringExtra(SearchManager.QUERY)?.also { query ->
            searchableActivityBinding.searchQueryTextView.text = query
        }
    }

}