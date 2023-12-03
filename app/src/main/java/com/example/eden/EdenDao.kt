package com.example.eden

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import androidx.room.Query
import androidx.room.Transaction
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.entities.relations.CommunityWithPosts
import com.example.eden.entities.relations.PostCommunityCrossRef
import com.example.eden.entities.relations.PostWithCommunities


@Dao
interface EdenDao {

    @Query("SELECT * FROM Post_Table")
    fun getAllPosts(): LiveData<List<Post>>

    @Upsert
    suspend fun upsertPost(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM Post_Table")
    fun deleteAllPosts(): Int

    @Query("SELECT * FROM Community_Table")
    fun getAllCommunities(): LiveData<List<Community>>

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

    @Transaction
    @Query("SELECT * FROM community_table WHERE communityId = :communityId")
    suspend fun getPostsOfCommunity(communityId: Int): List<CommunityWithPosts>

    @Transaction
    @Query("SELECT * FROM post_table WHERE postId = :postId")
    suspend fun getCommunitiesOfPost(postId: Int): List<PostWithCommunities>
}