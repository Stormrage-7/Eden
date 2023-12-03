package com.example.eden

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.databinding.ActivityNewPostBinding
import com.example.eden.databinding.ActivitySelectCommunityBinding

class SelectCommunityActivity: AppCompatActivity() {
    private lateinit var activitySelectCommunityBinding: ActivitySelectCommunityBinding
    private lateinit var databaseDao: EdenDao
    private lateinit var repository: AppRepository
    private lateinit var viewModel: CommunitiesViewModel
//    private lateinit var factory: SelectC
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySelectCommunityBinding = ActivitySelectCommunityBinding.inflate(layoutInflater)
        setContentView(activitySelectCommunityBinding.root)

        val viewModelFactory = CommunityViewModelFactory(repository, application as Eden)
        viewModel = ViewModelProvider(this, viewModelFactory)[CommunitiesViewModel::class.java]
        activitySelectCommunityBinding.communitiesViewModel = viewModel
        activitySelectCommunityBinding.lifecycleOwner = this

        val adapter = CommunityAdapter(context = this, object : CommunityAdapter.CommunityClickListener{
        override fun onClick(position: Int) {

        }

        override fun onJoinClick(position: Int) {
        }
    })

        viewModel.communityList.observe(this, Observer {
            it.let {
                adapter.updateAdapter(it)
            }
            if(activitySelectCommunityBinding.recyclerViewCommunitites.adapter!=adapter){
                activitySelectCommunityBinding.recyclerViewCommunitites.apply {
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

    }
}