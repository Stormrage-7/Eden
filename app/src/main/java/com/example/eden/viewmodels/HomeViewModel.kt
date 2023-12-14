package com.example.eden.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: AppRepository,
                    application: Eden
): AndroidViewModel(application) {

    val postList = repository.postList
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


    fun upvotePost(post: Post){
//        val post = postList.value!![position]

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
//        val post = postList.value!![position]

        val temp : Post = when(post.voteStatus){
            VoteStatus.UPVOTED -> post.copy(voteCounter = post.voteCounter-2, voteStatus = VoteStatus.DOWNVOTED)
            VoteStatus.DOWNVOTED -> post.copy(voteCounter = post.voteCounter+1, voteStatus = VoteStatus.NONE)
            VoteStatus.NONE -> post.copy(voteCounter = post.voteCounter-1, voteStatus = VoteStatus.DOWNVOTED)
        }

        viewModelScope.launch{
            repository.upsertPost(temp)
        }
    }

    private fun refreshPostListFromRepository(){
        viewModelScope.launch {
            repository.refreshPosts()
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