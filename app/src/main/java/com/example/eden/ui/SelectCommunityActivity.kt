package com.example.eden.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.database.AppRepository
import com.example.eden.ui.viewmodels.CommunitiesViewModel
import com.example.eden.adapters.CommunityAdapter
import com.example.eden.ui.viewmodels.CommunityViewModelFactory
import com.example.eden.Eden
import com.example.eden.database.EdenDao
import com.example.eden.databinding.ActivitySelectCommunityBinding
import com.example.eden.entities.Community

class SelectCommunityActivity: AppCompatActivity() {
    private lateinit var activitySelectCommunityBinding: ActivitySelectCommunityBinding
    private lateinit var databaseDao: EdenDao
    private lateinit var repository: AppRepository
    private lateinit var viewModel: CommunitiesViewModel
    private lateinit var factory: CommunityViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySelectCommunityBinding = ActivitySelectCommunityBinding.inflate(layoutInflater)
        setContentView(activitySelectCommunityBinding.root)


        repository = (application as Eden).repository
        factory = CommunityViewModelFactory(repository, application as Eden)
        viewModel = ViewModelProvider(this, factory)[CommunitiesViewModel::class.java]

        activitySelectCommunityBinding.communitiesViewModel = viewModel
        activitySelectCommunityBinding.lifecycleOwner = this

        val adapter = CommunityAdapter(context = this, object : CommunityAdapter.CommunityClickListener {
        override fun onClick(community: Community) {
            val output = Intent().apply {
                putExtra("CommunityObject", community)
            }
            setResult(RESULT_OK, output)
            finish()
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