package com.example.eden

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.eden.entities.Post

class AppRepository(private val database: AppDatabase) {

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