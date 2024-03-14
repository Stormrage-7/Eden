package com.example.eden.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Upsert
import androidx.room.Query
import androidx.room.Transaction
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.relations.JoinedCommunities
import com.example.eden.entities.Post
import com.example.eden.entities.User
import com.example.eden.entities.ImageUri
import com.example.eden.entities.relations.PostCommunityCrossRef
import com.example.eden.entities.relations.PostInteractions
import com.example.eden.entities.relations.PostWithCommunities
import com.example.eden.enums.VoteStatus
import com.example.eden.models.PostModel


@Dao
interface EdenDao {

    @Query("SELECT * FROM Post_Table ORDER BY postId DESC")
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
    fun getCommunityById(communityId: Int): LiveData<Community>

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

    @Query("SELECT * FROM post_table WHERE postId = :postId")
    fun getPostWithId(postId: Int): LiveData<Post>

    @Query("SELECT * FROM comments_table WHERE postId = :postId")
    fun getCommentListForPost(postId: Int): LiveData<List<Comment>>

    @Upsert
    suspend fun upsertComment(comment: Comment)

    /*************************** SEARCH QUERIES ****************************/
    @Query("SELECT * FROM post_table WHERE title LIKE '%' || :searchQuery || '%' OR bodyText LIKE '%' || :searchQuery || '%'")
    fun getPostsMatchingQuery(searchQuery: String): LiveData<List<Post>>
    @Query("SELECT * FROM community_table WHERE communityName LIKE '%' || :searchQuery || '%'")
    fun getCommunitiesMatchingQuery(searchQuery: String): LiveData<List<Community>>
    @Query("SELECT * FROM comments_table WHERE text LIKE '%' || :searchQuery || '%'")
    fun getCommentsMatchingQuery(searchQuery: String): LiveData<List<Comment>>


    /*************************** POST OF COMMUNITY *************************/

    @Query("SELECT * FROM Post_Table WHERE communityId = :communityId ORDER BY communityId DESC")
    fun getHotPostsOfCommunity(communityId: Int): LiveData<List<Post>>
    @Query("SELECT * FROM Post_Table WHERE communityId = :communityId ORDER BY voteCounter DESC")
    fun getTopPostsOfCommunity(communityId: Int): LiveData<List<Post>>
    @Query("SELECT * FROM Post_Table WHERE communityId = :communityId ORDER BY communityId ASC")
    fun getOldPostsOfCommunity(communityId: Int): LiveData<List<Post>>

    /************************************/
    @Query("UPDATE Community_Table SET noOfPosts = (noOfPosts+1) WHERE communityId = :communityId")
    suspend fun increasePostCount(communityId: Int)

    @Query("UPDATE Community_Table SET noOfPosts = noOfPosts-1 WHERE communityId = :communityId")
    suspend fun decreasePostCount(communityId: Int)

    @Query("SELECT * FROM post_table WHERE voteStatus = :voteStatus")
    fun getPosts(voteStatus: VoteStatus): LiveData<List<Post>>

    /******************* USER QUERIES ******************/
    @Query("SELECT * FROM user_table LIMIT 1")
    fun getUser(): LiveData<User>

    @Query("SELECT * FROM user_table WHERE userId = :userId")
    fun getUser(userId: Int): LiveData<User>

    @Upsert
    suspend fun upsertUser(user: User)

    @Upsert
    suspend fun upsertImgUri(imageUri: ImageUri)
    @Query("SELECT COUNT(id) FROM image_uri_table")
    fun getImgFileCounter(): LiveData<Int>


    @Upsert
    suspend fun upsertPostInteractions(postInteractions: PostInteractions)
    @Query("DELETE FROM Post_Interactions_Table WHERE postId = :postId AND userId = :userId")
    suspend fun deletePostInteractions(postId: Int, userId: Int)
    @Query("UPDATE Post_Table SET voteCounter = voteCounter+1 WHERE postId = :postId")
    suspend fun upvotePost(postId: Int)
    @Query("UPDATE Post_Table SET voteCounter = voteCounter-1 WHERE postId = :postId")
    suspend fun downvotePost(postId: Int)
    @Query("UPDATE Post_Table SET voteCounter = voteCounter-2 WHERE postId = :postId")
    suspend fun upvoteToDownvotePost(postId: Int)
    @Query("UPDATE Post_Table SET voteCounter = voteCounter+2 WHERE postId = :postId")
    suspend fun downvoteToUpvotePost(postId: Int)

    @Query("select Post_Table.postId, title, containsImage, isCustomImage, image_uri as imageUri, bodyText,voteCounter," +
            " COALESCE(PostInteractions.voteStatus, 'NONE') as voteStatus, " +
            "communityId, posterId from Post_Table left join (select * from Post_Interactions_Table where userId = :userId) as PostInteractions on Post_Table.postId == PostInteractions.postId")
    fun getPosts(userId: Int) : LiveData<List<PostModel>>

    @Query("select Post_Table.postId, title, containsImage, isCustomImage, image_uri as imageUri, bodyText,voteCounter," +
            " COALESCE(PostInteractions.voteStatus, 'NONE') as voteStatus, " +
            "communityId, posterId from Post_Table left join (select * from Post_Interactions_Table where userId = :userId) as PostInteractions on Post_Table.postId == PostInteractions.postId " +
            "WHERE communityId = :communityId")
    fun getPostsOfCommunity(communityId: Int, userId: Int): LiveData<List<PostModel>>

    @Query("select Post_Table.postId, title, containsImage, isCustomImage, image_uri as imageUri, bodyText,voteCounter," +
            " COALESCE(PostInteractions.voteStatus, 'NONE') as voteStatus, " +
            "communityId, posterId from Post_Table left join (select * from Post_Interactions_Table where userId = :userId) as PostInteractions on Post_Table.postId == PostInteractions.postId " +
            "WHERE PostInteractions.voteStatus = :voteStatus")
    fun getPosts(voteStatus: VoteStatus, userId: Int): LiveData<List<PostModel>>

    @Query("select Post_Table.postId, title, containsImage, isCustomImage, image_uri as imageUri, bodyText,voteCounter," +
            " COALESCE(PostInteractions.voteStatus, 'NONE') as voteStatus, " +
            "communityId, posterId from Post_Table left join (select * from Post_Interactions_Table where userId = :userId) as PostInteractions on Post_Table.postId == PostInteractions.postId " +
            "WHERE title LIKE '%' || :searchQuery || '%' OR bodyText LIKE '%' || :searchQuery || '%'")
    fun getPostsMatchingQuery(searchQuery: String, userId: Int): LiveData<List<PostModel>>

}