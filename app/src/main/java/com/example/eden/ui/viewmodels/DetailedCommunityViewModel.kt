package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.PostFilter
import com.example.eden.enums.VoteStatus
import kotlinx.coroutines.launch

class DetailedCommunityViewModel(private val repository: AppRepository,
                                 val communityObj: Community,
                                 application: Eden
): AndroidViewModel(application) {

    val community = repository.getCommunity(communityObj.communityId)
    var postList = repository.getPostsOfCommunity(communityObj.communityId)
    var filter: PostFilter = PostFilter.HOT
    init {
        Log.i("Testing", "DetailedCommunityViewModel Initialized")
    }

    fun upvotePost(post: Post){
        val temp : Post = when(post.voteStatus){
            VoteStatus.UPVOTED -> post.copy(voteCounter = post.voteCounter-1, voteStatus = VoteStatus.NONE)
            VoteStatus.DOWNVOTED -> post.copy(voteCounter = post.voteCounter+2, voteStatus = VoteStatus.UPVOTED)
            VoteStatus.NONE -> post.copy(voteCounter = post.voteCounter+1, voteStatus = VoteStatus.UPVOTED)
        }

        viewModelScope.launch {
            repository.upsertPost(temp)
        }
    }

    fun downvotePost(post: Post){
        val temp : Post = when(post.voteStatus){
            VoteStatus.UPVOTED -> post.copy(voteCounter = post.voteCounter-2, voteStatus = VoteStatus.DOWNVOTED)
            VoteStatus.DOWNVOTED -> post.copy(voteCounter = post.voteCounter+1, voteStatus = VoteStatus.NONE)
            VoteStatus.NONE -> post.copy(voteCounter = post.voteCounter-1, voteStatus = VoteStatus.DOWNVOTED)
        }

        viewModelScope.launch{
            repository.upsertPost(temp)
        }
    }

    fun onJoinClick() {
        val copy: Community = when(community.value!!.isJoined){
            true -> {
                viewModelScope.launch{
                    repository.deleteFromJoinedCommunities(community.value!!.communityId)
                }
                community.value!!.copy(noOfMembers = community.value!!.noOfMembers-1, isJoined = false)
            }

            false -> {
                viewModelScope.launch {
                    repository.insertIntoJoinedCommunities(community.value!!.communityId)
                }
                community.value!!.copy(noOfMembers = community.value!!.noOfMembers+1, isJoined = true)
            }
        }
        viewModelScope.launch{
            repository.upsertCommunity(copy)
        }
    }

    fun refreshPostListWithFilter(filter: PostFilter) {
        postList = repository.getPostsOfCommunity(community.value!!.communityId, filter)
        Log.i("DetailedCommunityViewModel", postList.value.toString())
    }

}