package com.example.eden

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
import com.example.eden.databinding.FragmentHomeBinding

class HomeFragment: Fragment(){
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
//    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: HomeViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflation
        fragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        val application = requireActivity().application as Eden
        repository = AppRepository(AppDatabase.getDatabase(application.applicationContext).edenDao())
        factory = HomeViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]


        viewModel = ViewModelProvider(this.requireActivity(), factory).get(HomeViewModel::class.java)
        Log.i("HomeFragment", viewModel.toString())

        fragmentHomeBinding.lifecycleOwner = this   // Important
        fragmentHomeBinding.homeViewModel = viewModel


        val adapter = context?.let {
            PostAdapter(context = it, object : PostAdapter.PostClickListener{
                // ANONYMOUS CLASS IMPLEMENTATION OF POSTCLICKLISTENER INTERFACE
            override fun onPostClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onUpvoteBtnClick(position: Int) {
                viewModel.upvotePost(position)
            }

            override fun onDownvoteBtnClick(position: Int) {
                viewModel.downvotePost(position)
            }

            override fun onPostLongClick(position: Int) {
                TODO("Not yet implemented")
            }

        }) }
        fragmentHomeBinding.rvPosts.adapter = adapter
        fragmentHomeBinding.rvPosts.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.rvPosts.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )

        viewModel.postList.observe(this.requireActivity(), Observer {
            it.let {
                if(it.isEmpty()){
                    fragmentHomeBinding.rvPosts.visibility = View.GONE
                    fragmentHomeBinding.tempImgView.visibility = View.VISIBLE
                    fragmentHomeBinding.tempTextView.visibility = View.VISIBLE
                }
                else {
                    fragmentHomeBinding.rvPosts.visibility = View.VISIBLE
                    fragmentHomeBinding.tempImgView.visibility = View.GONE
                    fragmentHomeBinding.tempTextView.visibility = View.GONE
                    adapter!!.update(it)
                    Log.i("Inside PostList Observer", it.toString())
                    Log.i("Inside PostList Observer", adapter.postList.toString())
                }
            }
        })


        return fragmentHomeBinding.root
    }




}