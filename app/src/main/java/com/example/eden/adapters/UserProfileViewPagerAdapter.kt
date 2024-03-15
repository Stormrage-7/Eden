package com.example.eden.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.eden.ui.DownvotedPostsFragment
import com.example.eden.ui.MyCommentsFragment
import com.example.eden.ui.UpvotedPostsFragment

class UserProfileViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> UpvotedPostsFragment()
            1 -> DownvotedPostsFragment()
            2 -> MyCommentsFragment()
            else -> Fragment()
        }
    }
}