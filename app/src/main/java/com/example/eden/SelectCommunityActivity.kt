package com.example.eden

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.databinding.ActivityNewPostBinding
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

        val adapter = CommunityAdapter(context = this, object : CommunityAdapter.CommunityClickListener{
        override fun onClick(community: Community) {
            val output = Intent().apply {
                putExtra("CommunityId", community.communityId)
                putExtra("CommunityName", community.communityName)
                putExtra("CommunityIsCustomImage", community.isCustomImage)
                putExtra("CommunityImageUri", community.imageUri)
//                if(community.containsImage) putExtra("CommunityImageSrc", community.imageSrc)
//                else putExtra("CommunityImageSrc", R.drawable.icon_logo)

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