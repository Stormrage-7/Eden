package com.example.eden

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: AppRepository,
                    application: Application): AndroidViewModel(application) {

    val postList = repository.postList

    init {
        Log.i("Testing", "HomeViewModel Initialized - ${postList.toString()}")
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
}