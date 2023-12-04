package com.example.eden

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.entities.relations.PostCommunityCrossRef
import kotlinx.coroutines.Dispatchers

class AppRepository(private val databaseDao: EdenDao) {

    var postList: LiveData<List<Post>> = databaseDao.getAllPosts()
    var communityList: LiveData<List<Community>> = databaseDao.getAllCommunities()

    init {
        refreshPosts()
        Log.i("Repo Creation", "${postList.value.toString()}")
        Log.i("Repo Creation", "${communityList.value.toString()}")
    }
    suspend fun upsertPost(post: Post): Long {
        return databaseDao.upsertPost(post)
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

    suspend fun insertPostCommunityCrossRef(postCommunityCrossRef: PostCommunityCrossRef) {
        databaseDao.upsertPostCommunityCrossRef(postCommunityCrossRef)
    }

    suspend fun getCommunityIdFromPostId(postId: Int): Int {
        return databaseDao.getCommunityIdFromPostId(postId)
    }

}