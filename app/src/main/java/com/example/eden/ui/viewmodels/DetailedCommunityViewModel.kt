package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import kotlinx.coroutines.launch

class DetailedCommunityViewModel(private val repository: AppRepository,
                                 val community: Community,
                                 application: Eden
): AndroidViewModel(application) {

    val postList = repository.getPostsOfCommunity(community.communityId)
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
        when(community.isJoined){
            true -> {
                viewModelScope.launch{
                    repository.deleteFromJoinedCommunities(community.communityId)
                }
                community.isJoined = false
            }
            false -> {
                viewModelScope.launch {
                    repository.insertIntoJoinedCommunities(community.communityId)
                }
                community.isJoined = true
            }
        }
        viewModelScope.launch{
            repository.upsertCommunity(community)
        }
    }

}