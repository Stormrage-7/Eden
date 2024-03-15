package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import com.example.eden.models.PostModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: AppRepository,
                    private val application: Eden
): AndroidViewModel(application) {

    val user = repository.getUser(application.userId)
    val postList = repository.getPostList(application.userId)
    val communityList = repository.getCommunityList(application.userId)
    val joinedCommunityList = repository.getJoinedCommunityList(application.userId)
    val joinedCommunityPostList = repository.getPostsOfJoinedCommunities(application.userId)
    val postCommunityCrossRefList = repository.postCommunityCrossRefList
    val userList = repository.getUserList()


    init {
        Log.i("Testing", "HomeViewModel Initialized - ${postList.toString()}")
        refreshPostListFromRepository()
        refreshPostCommunityCrossRefListFromRepository()
    }


    fun upvotePost(post: PostModel){
        when(post.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = post.postId, userId = application.userId, voteStatus = VoteStatus.NONE)
                repository.removePostUpvote(post.postId, post.posterId)}
            VoteStatus.DOWNVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = post.postId, userId = application.userId, voteStatus = VoteStatus.UPVOTED)
                repository.downvoteToUpvotePost(post.postId)}
            VoteStatus.NONE -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = post.postId, userId = application.userId, voteStatus = VoteStatus.UPVOTED)
                repository.upvotePost(post.postId, post.posterId)}
        }
    }

    fun downvotePost(post: PostModel){
        when(post.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, post.postId, VoteStatus.DOWNVOTED)
                repository.upvoteToDownvotePost(post.postId)}
            VoteStatus.DOWNVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, post.postId, VoteStatus.NONE)
                repository.removePostDownvote(post.postId, post.posterId)}
            VoteStatus.NONE -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, post.postId, VoteStatus.DOWNVOTED)
                repository.downvotePost(post.postId, post.posterId)}
        }
    }

    private fun refreshPostListFromRepository(){
        viewModelScope.launch {
            Log.i("Refresh Method", "Refreshed!")
        }
    }


    private fun refreshPostCommunityCrossRefListFromRepository() {
        viewModelScope.launch {
            repository.refreshPostCommunityCrossRef()
        }
    }


}