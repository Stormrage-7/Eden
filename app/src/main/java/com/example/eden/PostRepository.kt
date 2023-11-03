package com.example.eden

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PostRepository(private val database: AppDatabase) {

    var postList: LiveData<List<Post>> = database.postDao().getAll()

    suspend fun upsertPost(post: Post){
        database.postDao().upsertPost(post)
    }

    suspend fun refreshPosts(){
        postList = database.postDao().getAll()
    }

}