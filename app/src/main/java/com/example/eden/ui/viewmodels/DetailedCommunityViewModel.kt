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
import com.example.eden.models.CommunityModel
import com.example.eden.models.PostModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailedCommunityViewModel(private val repository: AppRepository,
                                 private val communityId: Int,
                                 private val application: Eden
): AndroidViewModel(application) {

    val community = repository.getCommunity(communityId, application.userId)
    var postList = repository.getPostsOfCommunity(communityId, application.userId)
    val userList = repository.getUserList()
    var filter: PostFilter = PostFilter.HOT
    init {
        Log.i("Testing", "DetailedCommunityViewModel Initialized")
    }

    fun upvotePost(post: PostModel){
//        val post = postList.value!![position]
        when(post.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = post.postId, userId = application.userId, voteStatus = VoteStatus.NONE, isBookMarked = post.isBookmarked)
                repository.removePostUpvote(post.postId, post.posterId)}
            VoteStatus.DOWNVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = post.postId, userId = application.userId, voteStatus = VoteStatus.UPVOTED, isBookMarked = post.isBookmarked)
                repository.downvoteToUpvotePost(post.postId)}
            VoteStatus.NONE -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = post.postId, userId = application.userId, voteStatus = VoteStatus.UPVOTED, isBookMarked = post.isBookmarked)
                repository.upvotePost(postId = post.postId, application.userId)}
        }
    }

    fun downvotePost(post: PostModel){
//        val post = postList.value!![position]
        when(post.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, post.postId, VoteStatus.DOWNVOTED, isBookMarked = post.isBookmarked)
                repository.upvoteToDownvotePost(post.postId)}
            VoteStatus.DOWNVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, post.postId, VoteStatus.NONE, isBookMarked = post.isBookmarked)
                repository.removePostDownvote(post.postId, post.posterId)}
            VoteStatus.NONE -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, post.postId, VoteStatus.DOWNVOTED, isBookMarked = post.isBookmarked)
                repository.downvotePost(postId = post.postId, application.userId)}
        }
    }

    fun bookmarkPost(post: PostModel){
        viewModelScope.launch { repository.updatePostInteractions(application.userId, post.postId, post.voteStatus,
            when(post.isBookmarked){
                true -> false
                false -> true
            }) }
    }

    fun onJoinClick() {
        when(community.value?.isJoined){
            true -> viewModelScope.launch{ repository.updateCommunityInteractions(application.userId, communityId, false)
                repository.unJoinCommunity(communityId) }
            false -> viewModelScope.launch { repository.updateCommunityInteractions(application.userId, communityId, true)
                repository.joinCommunity(communityId) }
            else -> {}
        }
    }

}