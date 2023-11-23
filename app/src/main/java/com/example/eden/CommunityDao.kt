package com.example.eden

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.entities.relations.CommunityWithPosts
import com.example.eden.entities.relations.PostCommunityCrossRef
import com.example.eden.entities.relations.PostWithCommunities

@Dao
interface CommunityDao {
    @Upsert
    suspend fun upsertCommunity(community: Community)

    @Delete
    suspend fun deleteCommunity(community: Community)

    @Upsert
    suspend fun upsertPostCommunityCrossRef(crossRef: PostCommunityCrossRef)

    @Transaction
    @Query("SELECT * FROM community_table WHERE communityId = :communityId")
    suspend fun getPostsOfCommunity(communityId: Int): List<CommunityWithPosts>

    @Transaction
    @Query("SELECT * FROM post_table WHERE postId = :postId")
    suspend fun getCommunitiesOfPost(postId: Int): List<PostWithCommunities>
}