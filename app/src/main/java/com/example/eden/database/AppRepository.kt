package com.example.eden.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.JoinedCommunities
import com.example.eden.entities.Post
import com.example.eden.entities.relations.PostCommunityCrossRef
import com.example.eden.enums.PostFilter
import com.example.eden.enums.VoteStatus

class AppRepository(private val databaseDao: EdenDao) {

    var postList: LiveData<List<Post>> = databaseDao.getAllPosts()
    private var _postList: MutableLiveData<List<Post>> = MutableLiveData()
    var postCommunityCrossRefList: LiveData<List<PostCommunityCrossRef>> = databaseDao.getAllPostCommunityCrossRef()
    var communityList: LiveData<List<Community>> = databaseDao.getAllCommunities()
    var joinedCommunitiesList: LiveData<List<Int>> = databaseDao.getAllJoinedCommunities()

    /********************* TESTING ***********************/
    fun getTopPostList(communityId: Int): LiveData<List<Post>> {
        return databaseDao.getTopPostsOfCommunity(communityId)
    }
    fun getHotPostList(communityId: Int): LiveData<List<Post>> {
        return databaseDao.getHotPostsOfCommunity(communityId)
    }
    fun getOldPostList(communityId: Int): LiveData<List<Post>> {
        return databaseDao.getOldPostsOfCommunity(communityId)
    }
    fun get_PostList(): LiveData<List<Post>> = _postList


    /*****************************************************/
    fun getCommentListForPost(postId: Int): LiveData<List<Comment>> = databaseDao.getCommentListForPost(postId)
    fun getPostsOfCommunity(communityId: Int): LiveData<List<Post>> = databaseDao.getPostsOfCommunity(communityId)

    fun getPostsOfCommunity(communityId: Int, filter: PostFilter): LiveData<List<Post>> {
        return when(filter){
            PostFilter.HOT -> databaseDao.getHotPostsOfCommunity(communityId)
            PostFilter.TOP -> databaseDao.getTopPostsOfCommunity(communityId)
            PostFilter.OLDEST -> databaseDao.getOldPostsOfCommunity(communityId)
        }
    }

    init {
        refreshPosts()
        refreshCommunities()
        refreshJoinedCommunities()
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

    fun refreshJoinedCommunities(){
        joinedCommunitiesList = databaseDao.getAllJoinedCommunities()
    }

    fun refreshPostCommunityCrossRef(){
        postCommunityCrossRefList = databaseDao.getAllPostCommunityCrossRef()
    }

    suspend fun insertPostCommunityCrossRef(postCommunityCrossRef: PostCommunityCrossRef) {
        databaseDao.upsertPostCommunityCrossRef(postCommunityCrossRef)
    }

    suspend fun getCommunityIdFromPostId(postId: Int): Int {
        return databaseDao.getCommunityIdFromPostId(postId)
    }

    suspend fun insertPost(post: Post): Int {
        return databaseDao.insertPost(post).toInt()
    }

    suspend fun insertIntoJoinedCommunities(communityId: Int) {
        databaseDao.insertJoinedCommunity(JoinedCommunities(0, communityId))
    }

    suspend fun deleteFromJoinedCommunities(communityId: Int) {
        databaseDao.deleteJoinedCommunity(communityId)
    }

    fun getPostWithId(postId: Int): LiveData<Post> {
        return databaseDao.getPostWithId(postId)
    }

    suspend fun upsertComment(comment: Comment) {
        databaseDao.upsertComment(comment)
    }

    /****************** SEARCH METHODS **********************/
    fun getPostsMatchingQuery(searchQuery: String): LiveData<List<Post>> {
        return databaseDao.getPostsMatchingQuery(searchQuery)
    }

    fun getCommunitiesMatchingQuery(searchQuery: String): LiveData<List<Community>> {
        return databaseDao.getCommunitiesMatchingQuery(searchQuery)
    }

    fun getCommentsMatchingQuery(searchQuery: String): LiveData<List<Comment>> {
        return databaseDao.getCommentsMatchingQuery(searchQuery)
    }

    fun getCommunity(communityId: Int): LiveData<Community> {
        return databaseDao.getCommunityById(communityId)
    }

    suspend fun increasePostCount(communityId: Int) {
        databaseDao.increasePostCount(communityId)
    }

    suspend fun decreasePostCount(communityId: Int) {
        databaseDao.decreasePostCount(communityId)
    }

    suspend fun deletePost(post: Post) {
        databaseDao.deletePost(post)
    }

    fun getUpvotedPosts(): LiveData<List<Post>> {
        return databaseDao.getPosts(VoteStatus.UPVOTED)
    }

    fun getDownvotedPosts(): LiveData<List<Post>> {
        return databaseDao.getPosts(VoteStatus.DOWNVOTED)
    }


}