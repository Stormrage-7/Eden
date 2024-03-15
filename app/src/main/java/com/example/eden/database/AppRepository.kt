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
import com.example.eden.entities.relations.CommentInteractions
import com.example.eden.entities.relations.CommunityInteractions
import com.example.eden.entities.relations.PostCommunityCrossRef
import com.example.eden.entities.relations.PostInteractions
import com.example.eden.enums.PostFilter
import com.example.eden.enums.VoteStatus
import com.example.eden.models.CommentModel
import com.example.eden.models.CommunityModel
import com.example.eden.models.PostModel

class AppRepository(private val databaseDao: EdenDao) {

    fun getPostList(userId: Int) = databaseDao.getPosts(userId)

    var postCommunityCrossRefList: LiveData<List<PostCommunityCrossRef>> = databaseDao.getAllPostCommunityCrossRef()
    fun getCommunityList(userId: Int) = databaseDao.getCommunities(userId)
    fun getJoinedCommunityList(userId: Int) = databaseDao.getJoinedCommunities(userId)
    fun getPostsOfJoinedCommunities(userId: Int) = databaseDao.getPostsOfJoinedCommunities(userId)


    /********************* TESTING ***********************/

    /*****************************************************/
    fun getCommentListForPost(postId: Int): LiveData<List<Comment>> = databaseDao.getCommentListForPost(postId)
    fun getPostsOfCommunity(communityId: Int, userId: Int): LiveData<List<PostModel>> = databaseDao.getPostsOfCommunity(communityId, userId)

//    fun getPostsOfCommunity(communityId: Int, filter: PostFilter): LiveData<List<Post>> {
//        return when(filter){
//            PostFilter.HOT -> databaseDao.getHotPostsOfCommunity(communityId)
//            PostFilter.TOP -> databaseDao.getTopPostsOfCommunity(communityId)
//            PostFilter.OLDEST -> databaseDao.getOldPostsOfCommunity(communityId)
//        }
//    }

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

    fun refreshPostCommunityCrossRef(){
        postCommunityCrossRefList = databaseDao.getAllPostCommunityCrossRef()
    }

    suspend fun insertPostCommunityCrossRef(postCommunityCrossRef: PostCommunityCrossRef) {
        databaseDao.upsertPostCommunityCrossRef(postCommunityCrossRef)
    }

//    suspend fun insertIntoJoinedCommunities(communityId: Int) {
//        databaseDao.insertJoinedCommunity(JoinedCommunities(0, communityId))
//    }

    suspend fun deleteFromJoinedCommunities(communityId: Int) {
        databaseDao.deleteJoinedCommunity(communityId)
    }

    fun getPostWithId(postId: Int, userId: Int): LiveData<PostModel> {
        return databaseDao.getPostWithId(postId, userId)
    }

    suspend fun upsertComment(comment: Comment) {
        databaseDao.upsertComment(comment)
    }

    /****************** SEARCH METHODS **********************/
    fun getPostsMatchingQuery(searchQuery: String, userId: Int): LiveData<List<PostModel>> {
        return databaseDao.getPostsMatchingQuery(searchQuery, userId)
    }

    fun getCommunitiesMatchingQuery(searchQuery: String, userId: Int): LiveData<List<CommunityModel>> {
        return databaseDao.getCommunitiesMatchingQuery(searchQuery, userId)
    }

    fun getCommentsMatchingQuery(searchQuery: String, userId: Int): LiveData<List<CommentModel>> {
        return databaseDao.getCommentsMatchingQuery(searchQuery, userId)
    }

    fun getCommunity(communityId: Int, userId: Int): LiveData<CommunityModel> {
        return databaseDao.getCommunityById(communityId, userId)
    }


    suspend fun increasePostCount(communityId: Int) {
        databaseDao.increasePostCount(communityId)
    }

    suspend fun decreasePostCount(communityId: Int) {
        databaseDao.decreasePostCount(communityId)
    }

    suspend fun deletePost(postId: Int) {
        databaseDao.deletePost(postId)
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

    fun getUser(userId: Int): LiveData<User>{
        return databaseDao.getUser(userId)
    }


    suspend fun upsertImgUri(imgUri: ImageUri) {
        databaseDao.upsertImgUri(imgUri)
    }
    fun getImgFileCounter(): LiveData<Int> = databaseDao.getImgFileCounter()
    fun getUserList(): LiveData<List<User>> = databaseDao.getUserList()
    fun getCommentsForPost(postId: Int, userId: Int): LiveData<List<CommentModel>> = databaseDao.getCommentsOfPost(postId, userId)

    //VoteStatus.NONE -> VoteStatues.UPVOTED
    suspend fun upvoteComment(commentId: Int){
        databaseDao.upvoteComment(commentId)
    }

    //VoteStatus.NONE -> VoteStatues.DOWNVOTED
    suspend fun downvoteComment(commentId: Int){
        databaseDao.downvoteComment(commentId)
    }

    //VoteStatus.UPVOTED -> VoteStatues.NONE
    suspend fun removeCommentUpvote(commentId: Int){
        databaseDao.downvoteComment(commentId)
    }

    //VoteStatus.DOWNVOTED -> VoteStatues.NONE
    suspend fun removeCommentDownvote(commentId: Int){
        databaseDao.upvoteComment(commentId)
    }

    //VoteStatus.DOWNVOTED -> VoteStatus.UPVOTED
    suspend fun downvoteToUpvoteComment(commentId: Int){
        databaseDao.downvoteToUpvoteComment(commentId)
    }

    //VoteStatus.UPVOTED -> VoteStatus.DOWNVOTED
    suspend fun upvoteToDownvoteComment(commentId: Int){
        databaseDao.upvoteToDownvoteComment(commentId)
    }

    suspend fun updateCommentInteractions(userId: Int, commentId: Int, voteStatus: VoteStatus){
        databaseDao.upsertCommentInteractions(CommentInteractions(userId, commentId, voteStatus))
    }

    //***** COMMUNITIES *****/
    //Not Joined -> Joined
    suspend fun joinCommunity(communityId: Int){
        databaseDao.increaseMemberCount(communityId)
    }

    //Joined -> Not Joined
    suspend fun unJoinCommunity(communityId: Int){
        databaseDao.decreaseMemberCount(communityId)
    }

    suspend fun updateCommunityInteractions(userId: Int, communityId: Int, isJoined: Boolean){
        databaseDao.upsertCommunityInteractions(CommunityInteractions(userId, communityId, isJoined))
    }

    suspend fun updatePost(postId: Int, bodyText: String) {
        databaseDao.updatePost(postId, bodyText)
    }

    suspend fun updateCommunity(communityId: Int, communityDescription: String) {
        databaseDao.updateCommunity(communityId, communityDescription)
    }

    fun getCommentsOfUser(userId: Int) = databaseDao.getCommentsOfUser(userId)


}