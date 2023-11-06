package com.example.eden

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PostRepository(private val database: AppDatabase) {

    var postList: LiveData<List<Post>> = database.postDao().getAll()

    init {
        Log.i("Repo Creation", "${postList.toString()}")
    }
    suspend fun upsertPost(post: Post){
        database.postDao().upsertPost(post)
    }

    suspend fun refreshPosts(){
        postList = database.postDao().getAll()
    }

}