package com.example.eden.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.adapters.PostInteractionsViewPagerAdapter
import com.example.eden.adapters.ViewPagerAdapter
import com.example.eden.databinding.ActivityPostInteractionBinding
import com.example.eden.ui.viewmodels.PostInteractionsViewModel
import com.example.eden.ui.viewmodels.PostInteractionsViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator

class PostInteractionsActivity: AppCompatActivity() {
    private lateinit var activityPostInteractionBinding: ActivityPostInteractionBinding
    lateinit var viewModel: PostInteractionsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        activityPostInteractionBinding = ActivityPostInteractionBinding.inflate(layoutInflater)
        setContentView(activityPostInteractionBinding.root)
        val application = application as Eden
        val factory = PostInteractionsViewModelFactory(application.repository, application)
        viewModel = ViewModelProvider(this, factory)[PostInteractionsViewModel::class.java]

        val viewPagerAdapter = PostInteractionsViewPagerAdapter(supportFragmentManager, lifecycle)
        activityPostInteractionBinding.postInteractionsViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(activityPostInteractionBinding.tabLayout, activityPostInteractionBinding.postInteractionsViewPager){ tab, position ->
            when(position){
                0 -> tab.text = "Upvoted"
                1 -> tab.text = "Downvoted"
                2 -> tab.text = "Comments"
            }
        }.attach()

        activityPostInteractionBinding.closeBtn.setOnClickListener {
            finish()
        }
    }

}