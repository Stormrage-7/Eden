package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.Eden
import com.example.eden.database.AppRepository
import com.example.eden.entities.Community
import kotlinx.coroutines.launch

class PostInteractionsViewModel(private val repository: AppRepository,
                                application: Eden
): AndroidViewModel(application) {

    var upvotedPostList = repository.getUpvotedPosts(application.userId)
    var downvotedPostList = repository.getDownvotedPosts(application.userId)
    val communityList = repository.communityList

    init {
        Log.i("PostInteractions", "PostInteractionsViewModel Initialized!")
    }


//    fun refreshPostList(){
//        viewModelScope.launch {
//            postList = repository.getPostsMatchingQuery(searchQuery)
//            Log.i("Refresh Method", "Posts Refreshed!")
//        }
//    }
//
//    fun refreshCommunityList(){
//        viewModelScope.launch {
//            communityList = repository.getCommunitiesMatchingQuery(searchQuery)
//            Log.i("Refresh Method", "Communities Refreshed!")
//
//        }
//    }
//
//    fun refreshDataSet(){
//        viewModelScope.launch {
//            postList = repository.getPostsMatchingQuery(searchQuery)
//            communityList = repository.getCommunitiesMatchingQuery(searchQuery)
//            commentList = repository.getCommentsMatchingQuery(searchQuery)
//        }
//    }

}