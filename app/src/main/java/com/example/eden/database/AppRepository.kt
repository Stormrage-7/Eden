package com.example.eden.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.relations.JoinedCommunities
import com.example.eden.entities.Post
import com.example.eden.entities.User
import com.example.eden.entities.ImageUri
import com.example.eden.entities.relations.PostCommunityCrossRef
import com.example.eden.entities.relations.PostInteractions
import com.example.eden.enums.PostFilter
import com.example.eden.enums.VoteStatus
import com.example.eden.models.PostModel

class AppRepository(private val databaseDao: EdenDao) {

    var postList: LiveData<List<PostModel>> = databaseDao.getPosts(1)
    fun getPostList(userId: Int) = databaseDao.getPosts(userId)
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
    fun getPostsOfCommunity(communityId: Int, userId: Int): LiveData<List<PostModel>> = databaseDao.getPostsOfCommunity(communityId, userId)

    fun getPostsOfCommunity(communityId: Int, filter: PostFilter): LiveData<List<Post>> {
        return when(filter){
            PostFilter.HOT -> databaseDao.getHotPostsOfCommunity(communityId)
            PostFilter.TOP -> databaseDao.getTopPostsOfCommunity(communityId)
            PostFilter.OLDEST -> databaseDao.getOldPostsOfCommunity(communityId)
        }
    }

    init {
//        refreshPosts()
        refreshCommunities()
        refreshJoinedCommunities()
        Log.i("Repo Creation", "${postList.value.toString()}")
        Log.i("Repo Creation", "${communityList.value.toString()}")
    }
    suspend fun upsertPost(post: Post): Long {
        return databaseDao.upsertPost(post)
    }

    //VoteStatus.NONE -> VoteStatues.UPVOTED
    suspend fun upvotePost(postId: Int, userId: Int){
        databaseDao.upvotePost(postId)
    }

    //VoteStatus.NONE -> VoteStatues.DOWNVOTED
    suspend fun downvotePost(postId: Int, userId: Int){
        databaseDao.downvotePost(postId)
    }

    //VoteStatus.UPVOTED -> VoteStatues.NONE
    suspend fun removePostUpvote(postId: Int, userId: Int){
        databaseDao.downvotePost(postId)
    }

    //VoteStatus.DOWNVOTED -> VoteStatues.NONE
    suspend fun removePostDownvote(postId: Int, userId: Int){
        databaseDao.upvotePost(postId)
    }

    suspend fun downvoteToUpvotePost(postId: Int){
        databaseDao.downvoteToUpvotePost(postId)
    }
    suspend fun upvoteToDownvotePost(postId: Int){
        databaseDao.upvoteToDownvotePost(postId)
    }

    suspend fun updatePostInteractions(userId: Int, postId: Int, voteStatus: VoteStatus){
        databaseDao.upsertPostInteractions(PostInteractions(userId, postId, voteStatus))
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
    fun getPostsMatchingQuery(searchQuery: String, userId: Int): LiveData<List<PostModel>> {
        return databaseDao.getPostsMatchingQuery(searchQuery, userId)
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

    fun getUpvotedPosts(userId: Int): LiveData<List<PostModel>> {
        return databaseDao.getPosts(VoteStatus.UPVOTED, userId)
    }

    fun getDownvotedPosts(userId: Int): LiveData<List<PostModel>> {
        return databaseDao.getPosts(VoteStatus.DOWNVOTED, userId)
    }

    suspend fun upsertUser(user: User) {
        databaseDao.upsertUser(user)
    }

    fun getUser(): LiveData<User> {
        return databaseDao.getUser()
    }


    suspend fun upsertImgUri(imgUri: ImageUri) {
        databaseDao.upsertImgUri(imgUri)
    }
    fun getImgFileCounter(): LiveData<Int> = databaseDao.getImgFileCounter()


}