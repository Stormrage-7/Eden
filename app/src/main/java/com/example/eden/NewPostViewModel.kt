package com.example.eden

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.eden.entities.Post
import com.example.eden.entities.relations.PostCommunityCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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