package com.example.eden

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.eden.entities.Community
import com.example.eden.entities.Post

class AppRepository(private val databaseDao: EdenDao) {

    var postList: LiveData<List<Post>> = databaseDao.getAllPosts()
    var communityList: LiveData<List<Community>> = databaseDao.getAllCommunities()

    init {
        Log.i("Repo Creation", "${postList.toString()}")
    }
    suspend fun upsertPost(post: Post){
        databaseDao.upsertPost(post)
    }

    fun refreshPosts(){
        postList = databaseDao.getAllPosts()
    }

    suspend fun upsertCommunity(community: Community){
        databaseDao.upsertCommunity(community)
    }
    fun refreshCommunities() {
        communityList = databaseDao.getAllCommunities()
    }

}