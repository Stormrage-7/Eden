package com.example.eden.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.eden.ui.CommentSearchFragment
import com.example.eden.ui.CommunitySearchFragment
import com.example.eden.ui.PostSearchFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> PostSearchFragment()
            1 -> CommunitySearchFragment()
            2 -> CommentSearchFragment()
            else -> Fragment()
        }
    }
}