package com.example.eden.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.ui.viewmodels.CommunitiesViewModel
import com.example.eden.adapters.CommunityAdapter
import com.example.eden.ui.viewmodels.CommunityViewModelFactory
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.databinding.FragmentCommunitiesBinding
import com.example.eden.entities.Community

class CommunitiesFragment: Fragment() {
    private lateinit var fragmentCommunitiesBinding: FragmentCommunitiesBinding
    private lateinit var viewModel: CommunitiesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        fragmentCommunitiesBinding = FragmentCommunitiesBinding.inflate(layoutInflater)
        fragmentCommunitiesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_communities, container, false)

        val application = this.requireActivity().application.applicationContext as Eden
        val repository = application.repository
        val viewModelFactory = CommunityViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[CommunitiesViewModel::class.java]

        fragmentCommunitiesBinding.communitiesViewModel = viewModel
        fragmentCommunitiesBinding.lifecycleOwner = this.requireActivity() //Very Important line of code that allowed LiveData to update the layout

        val adapter = CommunityAdapter(context = this.requireActivity(), object : CommunityAdapter.CommunityClickListener {
            override fun onClick(community: Community) {
                Intent(requireActivity(), CommunityDetailedActivity:: class.java).apply {
                    putExtra("CommunityObject", community)
                    startActivity(this)
                }
            }

            override fun onJoinClick(position: Int) {
                viewModel.onJoinClick(position)
            }

        })


        viewModel.communityList.observe(this.requireActivity(), Observer{
            it.let {
                Log.i("CommunitiesFragment", "${it.toString()}")
                adapter!!.updateAdapter(it)
            }
            if(fragmentCommunitiesBinding.rvCommunities.adapter!=adapter){
                fragmentCommunitiesBinding.rvCommunities.apply {
                    this.adapter = adapter
                    this.layoutManager = LinearLayoutManager(context)
                    addItemDecoration(
                        DividerItemDecoration(
                            context,
                            LinearLayoutManager(context).orientation
                        )
                    )
                }
            }
        })

        return fragmentCommunitiesBinding.root
    }
}