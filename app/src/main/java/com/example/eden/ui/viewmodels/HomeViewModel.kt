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
    val communityList = repository.communityList
    val postCommunityCrossRefList = repository.postCommunityCrossRefList
    val joinedCommunitiesList = repository.joinedCommunitiesList
    lateinit var detailedPost: LiveData<Post>
//    var localCommunityList = listOf<Community>()
//    var communityId: Int = -1

    init {
        Log.i("Testing", "HomeViewModel Initialized - ${postList.toString()}")
        refreshCommunityListFromRepository()
        refreshPostListFromRepository()
        refreshPostCommunityCrossRefListFromRepository()
        refreshJoinedCommunitiesListFromRepository()
    }


    fun upvotePost(post: PostModel){
//        val post = postList.value!![position]
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
//        val post = postList.value!![position]
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

    fun refreshCommunityListFromRepository(){
        viewModelScope.launch {
            repository.refreshCommunities()
            Log.i("Refresh Method", "Communities Refreshed! ${communityList.value.toString()}")

        }
    }

    private fun refreshPostCommunityCrossRefListFromRepository() {
        viewModelScope.launch {
            repository.refreshPostCommunityCrossRef()
        }
    }

    private fun refreshJoinedCommunitiesListFromRepository() {
        viewModelScope.launch{
            repository.refreshJoinedCommunities()
        }
    }

}