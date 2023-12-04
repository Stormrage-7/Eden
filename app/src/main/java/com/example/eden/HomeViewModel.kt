package com.example.eden

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class HomeViewModel(private val repository: AppRepository,
                    application: Eden): AndroidViewModel(application) {

    val postList = repository.postList
    val communityList = repository.communityList
//    var localCommunityList = listOf<Community>()
//    var communityId: Int = -1

    init {
        Log.i("Testing", "HomeViewModel Initialized - ${postList.toString()}")
        refreshCommunityListFromRepository()
        refreshPostListFromRepository()
    }

    fun upvotePost(position: Int){
        val post = postList.value!![position]

        val temp : Post = when(post.voteStatus){
            VoteStatus.UPVOTED -> post.copy(voteCounter = post.voteCounter-1, voteStatus = VoteStatus.NONE)
            VoteStatus.DOWNVOTED -> post.copy(voteCounter = post.voteCounter+2, voteStatus = VoteStatus.UPVOTED)
            VoteStatus.NONE -> post.copy(voteCounter = post.voteCounter+1, voteStatus = VoteStatus.UPVOTED)
        }

        viewModelScope.launch {
            repository.upsertPost(temp)
        }
    }

    fun downvotePost(position: Int){
        val post = postList.value!![position]

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


//    fun getCommunityIdFromPostId(position: Int) {
//        val postId = postList.value!![position].postId
//        val communityId = viewModelScope.async{
//            repository.getCommunityIdFromPostId(postId)
//        }
//        communityId.invokeOnCompletion {
//            if (it == null){
//
//            }
//        }
//    }

}