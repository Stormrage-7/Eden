package com.example.eden.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.eden.ui.CommunitySearchFragment
import com.example.eden.ui.DownvotedPostsFragment
import com.example.eden.ui.UpvotedPostsFragment

class PostInteractionsViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> UpvotedPostsFragment()
            1 -> DownvotedPostsFragment()
            else -> Fragment()
        }
    }
}