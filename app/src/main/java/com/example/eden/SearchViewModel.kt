package com.example.eden

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: AppRepository,
                      var searchQuery: String,
                      application: Eden): AndroidViewModel(application) {

    var postList = repository.getPostsMatchingQuery(searchQuery)
    var communityList = repository.getCommunitiesMatchingQuery(searchQuery)
    val allCommunityList = repository.communityList

    init {
        Log.i("SearchViewModel", "SearchViewModel Initialized!")
    }


    fun refreshPostList(){
        viewModelScope.launch {
            postList = repository.getPostsMatchingQuery(searchQuery)
            Log.i("Refresh Method", "Posts Refreshed!")
        }
    }

    fun refreshCommunityList(){
        viewModelScope.launch {
            communityList = repository.getCommunitiesMatchingQuery(searchQuery)
            Log.i("Refresh Method", "Communities Refreshed!")

        }
    }

    fun onJoinClick(position: Int) {
        val community = communityList.value!![position]
        val temp: Community = when(community.isJoined){
            true -> {
                viewModelScope.launch{
                    repository.deleteFromJoinedCommunities(community.communityId)
                }
                community.copy(noOfMembers = community.noOfMembers-1, isJoined = false)
            }
            false -> {
                viewModelScope.launch {
                    repository.insertIntoJoinedCommunities(community.communityId)
                }
                community.copy(noOfMembers = community.noOfMembers+1, isJoined = true)
            }
        }
        viewModelScope.launch{
            repository.upsertCommunity(temp)
        }
    }

}