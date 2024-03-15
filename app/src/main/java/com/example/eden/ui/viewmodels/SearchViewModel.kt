package com.example.eden.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Community
import kotlinx.coroutines.launch

@SuppressLint("LogNotTimber")
class SearchViewModel(private val repository: AppRepository,
                      var searchQuery: String,
                      private val application: Eden
): AndroidViewModel(application) {

    var postList = repository.getPostsMatchingQuery(searchQuery, application.userId)
    var communityList = repository.getCommunitiesMatchingQuery(searchQuery, application.userId)
    var commentList = repository.getCommentsMatchingQuery(searchQuery, application.userId)
    val allCommunityList = repository.getCommunityList(application.userId)
    var userList = repository.getUserList()

    init {
        Log.i("SearchViewModel", "SearchViewModel Initialized!")
    }


//    fun refreshCommunityList(){
//        viewModelScope.launch {
//            communityList = repository.getCommunitiesMatchingQuery(searchQuery)
//            Log.i("Refresh Method", "Communities Refreshed!")
//
//        }
//    }

    fun refreshDataSet(){
        viewModelScope.launch {
            postList = repository.getPostsMatchingQuery(searchQuery, application.userId)
            communityList = repository.getCommunitiesMatchingQuery(searchQuery, application.userId)
            commentList = repository.getCommentsMatchingQuery(searchQuery, application.userId)
        }
    }

    fun onJoinClick(position: Int) {
        val community = communityList.value!![position]
        when(community.isJoined){
            true -> viewModelScope.launch{ repository.updateCommunityInteractions(application.userId, community.communityId, false)
                repository.unJoinCommunity(community.communityId) }
            false -> viewModelScope.launch { repository.updateCommunityInteractions(application.userId, community.communityId, true)
                repository.joinCommunity(community.communityId) }
        }
    }

}