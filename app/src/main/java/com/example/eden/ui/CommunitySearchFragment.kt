package com.example.eden.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.adapters.CommunityAdapter
import com.example.eden.R
import com.example.eden.ui.viewmodels.SearchViewModel
import com.example.eden.databinding.FragmentCommunitySearchBinding
import com.example.eden.entities.Community
import com.example.eden.models.CommunityModel

class CommunitySearchFragment: Fragment() {
    private lateinit var fragmentCommunitySearchBinding: FragmentCommunitySearchBinding
    private lateinit var viewModel: SearchViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCommunitySearchBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_community_search, container, false
        )
        viewModel = (activity as SearchableActivity).viewModel

        fragmentCommunitySearchBinding.lifecycleOwner = this.viewLifecycleOwner

        val adapter = CommunityAdapter(context = requireContext(), object : CommunityAdapter.CommunityClickListener {
            override fun onClick(community: CommunityModel) {
                Intent(activity as SearchableActivity, CommunityDetailedActivity:: class.java).apply {
                    putExtra("CommunityObject", community)
                    startActivity(this)
                }
            }

            override fun onJoinClick(position: Int) {
                if (viewModel.communityList.value?.get(position)?.isJoined == true){
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@CommunitySearchFragment.requireContext())
                    builder
                        .setMessage("Are you sure you want to leave this community?")
                        .setPositiveButton("Yes") { _, _ ->
                            viewModel.onJoinClick(position)
                        }
                        .setNegativeButton("No") { _, _ ->
                        }

                    val confirmationDialog: AlertDialog = builder.create()
                    confirmationDialog.show()
                }
                else viewModel.onJoinClick(position)
            }

        })

        fragmentCommunitySearchBinding.rvCommunities.adapter = adapter
        fragmentCommunitySearchBinding.rvCommunities.layoutManager = LinearLayoutManager(requireContext())
        fragmentCommunitySearchBinding.rvCommunities.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager(context).orientation
            )
        )

        viewModel.communityList.observe(activity as SearchableActivity) {
            it.let {
                if (it.isEmpty()) {
                    fragmentCommunitySearchBinding.rvCommunities.visibility = View.GONE
                    fragmentCommunitySearchBinding.tempImgView.visibility = View.VISIBLE
                    fragmentCommunitySearchBinding.tempTextView.visibility = View.VISIBLE
                    adapter.updateAdapter(it)
                } else {
                    fragmentCommunitySearchBinding.rvCommunities.visibility = View.VISIBLE
                    fragmentCommunitySearchBinding.tempImgView.visibility = View.GONE
                    fragmentCommunitySearchBinding.tempTextView.visibility = View.GONE
                    adapter.updateAdapter(it)
                }
            }
        }

        return fragmentCommunitySearchBinding.root
    }
}