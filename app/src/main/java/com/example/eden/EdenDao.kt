package com.example.eden

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Upsert
import androidx.room.Query
import androidx.room.Transaction
import com.example.eden.entities.Community
import com.example.eden.entities.JoinedCommunities
import com.example.eden.entities.Post
import com.example.eden.entities.relations.CommunityWithPosts
import com.example.eden.entities.relations.PostCommunityCrossRef
import com.example.eden.entities.relations.PostWithCommunities


@Dao
interface EdenDao {

    @Query("SELECT * FROM Post_Table")
    fun getAllPosts(): LiveData<List<Post>>

    @Insert
    suspend fun insertPost(post: Post): Long

    @Upsert
    suspend fun upsertPost(post: Post): Long

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM Post_Table")
    fun deleteAllPosts(): Int

    @Insert
    suspend fun insertJoinedCommunity(joinedCommunities: JoinedCommunities)

    @Query("DELETE FROM Joined_Communities_Table WHERE communityId = :communityId")
    suspend fun deleteJoinedCommunity(communityId: Int)

    @Query("SELECT communityId FROM Joined_Communities_Table")
    fun getAllJoinedCommunities(): LiveData<List<Int>>

    @Query("SELECT * FROM Community_Table")
    fun getAllCommunities(): LiveData<List<Community>>

    @Query("SELECT * FROM Community_Table WHERE communityId = :communityId")
    suspend fun getCommunityById(communityId: Int): Community

    @Query("SELECT communityId FROM PostCommunityCrossRef WHERE postId = :postId")
    suspend fun getCommunityIdFromPostId(postId: Int): Int

    @Query("SELECT COUNT(communityId) FROM community_table")
    fun getCommunityCount(): Int

    @Upsert
    suspend fun upsertCommunity(community: Community)

    @Delete
    suspend fun deleteCommunity(community: Community)

    @Query("DELETE FROM community_table")
    fun deleteAllCommunities()

    @Upsert
    suspend fun upsertPostCommunityCrossRef(crossRef: PostCommunityCrossRef)

    @Query("SELECT * FROM PostCommunityCrossRef")
    fun getAllPostCommunityCrossRef(): LiveData<List<PostCommunityCrossRef>>

    @Transaction
    @Query("SELECT * FROM community_table WHERE communityId = :communityId")
    suspend fun getPostsOfCommunity(communityId: Int): List<CommunityWithPosts>

    @Transaction
    @Query("SELECT * FROM post_table WHERE postId = :postId")
    suspend fun getCommunitiesOfPost(postId: Int): List<PostWithCommunities>
}