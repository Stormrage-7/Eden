package com.example.eden

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eden.databinding.FragmentCommunitiesBinding

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
        viewModel = ViewModelProvider(this).get(CommunitiesViewModel::class.java)

        fragmentCommunitiesBinding.communitiesViewModel = viewModel
        fragmentCommunitiesBinding.lifecycleOwner = this.requireActivity() //Very Important line of code that allowed LiveData to update the layout

//        viewModel.setCommunityList(communityList)

        val adapter = context?.let {
            CommunityAdapter(context = it, object : CommunityAdapter.CommunityClickListener{
                override fun onClick(position: Int) {
                    //TODO
                }

                override fun onJoinClick(position: Int) {
                    viewModel.onJoinClick(position)
                }

            }) }

        fragmentCommunitiesBinding.rvCommunities.apply {


        }

        viewModel.communityList.observe(this.requireActivity(), Observer{
            it.let {
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

//    private fun updateCountText(){
//        fragmentCommunitiesBinding.textViewCount.text = viewModel.count.toString()
//    }
}