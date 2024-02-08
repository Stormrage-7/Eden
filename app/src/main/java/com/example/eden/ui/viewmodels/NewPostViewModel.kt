package com.example.eden.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.entities.relations.PostCommunityCrossRef
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class NewPostViewModel(private val repository: AppRepository,
                       application: Application): AndroidViewModel(application) {

    var postId: Int = -1
    init {
        Log.i("NewPostViewModel", "NewPostViewModel Initialized!")
    }

    fun upsertPost(post: Post): Int{
//        val result = MutableLiveData<Int>()
        viewModelScope.launch {
            postId = repository.upsertPost(post).toInt()
        }
        return postId
    }

    fun upsertPost(post: Post, communityId: Int): Int{
        viewModelScope.launch {
            val postId1 = async{
                repository.upsertPost(post) }
            repository.insertPostCommunityCrossRef(PostCommunityCrossRef(postId1.await().toInt(), communityId))
            repository.increasePostCount(communityId)
        }
        return 1
    }

    fun insertPostCommunityCrossRef(postCommunityCrossRef: PostCommunityCrossRef){
        viewModelScope.launch {
            repository.insertPostCommunityCrossRef(postCommunityCrossRef)
        }
    }

//    fun insertPost(post: Post): Int {
//        viewModelScope.launch(Dispatchers.Default) {
//            postId = repository.insertPost(post)
//        }
//        return postId
//    }

}