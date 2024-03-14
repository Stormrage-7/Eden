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
import com.example.eden.models.PostModel
import kotlinx.coroutines.launch

class DetailedCommunityViewModel(private val repository: AppRepository,
                                 communityObj: Community,
                                 application: Eden
): AndroidViewModel(application) {

    val community = repository.getCommunity(communityObj.communityId)
    var postList = repository.getPostsOfCommunity(communityObj.communityId, application.userId)
    var filter: PostFilter = PostFilter.HOT
    init {
        Log.i("Testing", "DetailedCommunityViewModel Initialized")
    }

    fun upvotePost(post: PostModel){
        when(post.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch { repository.removePostUpvote(post.postId, post.posterId) }
            VoteStatus.DOWNVOTED -> viewModelScope.launch {
                repository.removePostDownvote(post.postId, post.posterId)
                repository.upvotePost(post.postId, post.posterId)
            }
            VoteStatus.NONE -> viewModelScope.launch { repository.upvotePost(post.postId, post.posterId) }
        }
    }

    fun downvotePost(post: PostModel){
        when(post.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch {
                repository.removePostUpvote(post.postId, post.posterId)
                repository.downvotePost(post.postId, post.posterId)
            }
            VoteStatus.DOWNVOTED -> viewModelScope.launch { repository.removePostDownvote(post.postId, post.posterId) }
            VoteStatus.NONE -> viewModelScope.launch { repository.downvotePost(post.postId, post.posterId) }
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

}