package com.example.eden.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Upsert
import androidx.room.Query
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
import com.example.eden.enums.VoteStatus
import com.example.eden.models.CommentModel
import com.example.eden.models.CommunityModel
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

    @Query("DELETE FROM Post_Table WHERE postId = :postId")
    suspend fun deletePost(postId: Int)

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

    @Query("SELECT * FROM comments_table WHERE postId = :postId")
    fun getCommentListForPost(postId: Int): LiveData<List<Comment>>

    @Upsert
    suspend fun upsertComment(comment: Comment)

    /*************************** SEARCH QUERIES ****************************/
    @Query("SELECT * FROM community_table WHERE communityName LIKE '%' || :searchQuery || '%'")
    fun getCommunitiesMatchingQuery(searchQuery: String): LiveData<List<Community>>

    /*************************** POST OF COMMUNITY *************************/

    @Query("SELECT * FROM Post_Table WHERE communityId = :communityId ORDER BY communityId DESC")
    fun getHotPostsOfCommunity(communityId: Int): LiveData<List<Post>>
    @Query("SELECT * FROM Post_Table WHERE communityId = :communityId ORDER BY voteCounter DESC")
    fun getTopPostsOfCommunity(communityId: Int): LiveData<List<Post>>
    @Query("SELECT * FROM Post_Table WHERE communityId = :communityId ORDER BY communityId ASC")
    fun getOldPostsOfCommunity(communityId: Int): LiveData<List<Post>>

    /************************************/
    @Query("SELECT * FROM post_table WHERE voteStatus = :voteStatus")
    fun getPosts(voteStatus: VoteStatus): LiveData<List<Post>>

    /******************* USER QUERIES ******************/
    @Query("SELECT * FROM user_table LIMIT 1")
    fun getUser(): LiveData<User>

    @Query("SELECT * FROM user_table WHERE userId = :userId")
    fun getUser(userId: Int): LiveData<User>

    @Query("SELECT * FROM user_table")
    fun getUserList(): LiveData<List<User>>

    @Upsert
    suspend fun upsertUser(user: User)

    @Upsert
    suspend fun upsertImgUri(imageUri: ImageUri)
    @Query("SELECT COUNT(id) FROM image_uri_table")
    fun getImgFileCounter(): LiveData<Int>


    //****** NEW POST INTERACTIONS *****//
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

    @Query("select Post_Table.postId, title, containsImage, isCustomImage, image_uri as imageUri, bodyText,voteCounter," +
            " COALESCE(PostInteractions.voteStatus, 'NONE') as voteStatus, " +
            "communityId, posterId from Post_Table left join (select * from Post_Interactions_Table where userId = :userId) as PostInteractions on Post_Table.postId == PostInteractions.postId " +
            "WHERE Post_Table.postId = :postId")
    fun getPostWithId(postId: Int, userId: Int): LiveData<PostModel>

    //****** NEW COMMENT INTERACTIONS *****//

    @Upsert
    suspend fun upsertCommentInteractions(commentInteractions: CommentInteractions)

    @Query("DELETE FROM Comment_Interactions_Table WHERE commentId = :commentId AND userId = :userId")
    suspend fun deleteCommentInteractions(commentId: Int, userId: Int)
    @Query("UPDATE Comments_Table SET voteCounter = voteCounter+1 WHERE commentId = :commentId")
    suspend fun upvoteComment(commentId: Int)
    @Query("UPDATE Comments_Table SET voteCounter = voteCounter-1 WHERE commentId = :commentId")
    suspend fun downvoteComment(commentId: Int)
    @Query("UPDATE Comments_Table SET voteCounter = voteCounter-2 WHERE commentId = :commentId")
    suspend fun upvoteToDownvoteComment(commentId: Int)
    @Query("UPDATE Comments_Table SET voteCounter = voteCounter+2 WHERE commentId = :commentId")
    suspend fun downvoteToUpvoteComment(commentId: Int)

    @Query("select Comments_Table.commentId, commentText, imageUri, voteCounter, " +
            "COALESCE(CommentInteractions.voteStatus, 'NONE') as voteStatus, " +
            "postId, communityId, posterId from Comments_Table left join (select * from Comment_Interactions_Table where userId = :userId) as CommentInteractions on Comments_Table.commentId == CommentInteractions.commentId " +
            "WHERE postId = :postId")
    fun getCommentsOfPost(postId: Int, userId: Int): LiveData<List<CommentModel>>

    @Query("select Comments_Table.commentId, commentText, imageUri, voteCounter, " +
            "COALESCE(CommentInteractions.voteStatus, 'NONE') as voteStatus, " +
            "postId, communityId, posterId from Comments_Table left join (select * from Comment_Interactions_Table where userId = :userId) as CommentInteractions on Comments_Table.commentId == CommentInteractions.commentId " +
            "WHERE commentText LIKE '%' || :searchQuery || '%'")
    fun getCommentsMatchingQuery(searchQuery: String, userId: Int): LiveData<List<CommentModel>>

    //****** NEW COMMUNITY INTERACTIONS *****//

    @Upsert
    suspend fun upsertCommunityInteractions(communityInteractions: CommunityInteractions)

    @Query("select Community_Table.communityId, communityName, description, noOfMembers, noOfPosts, " +
            "contains_image as isCustomImage, image_uri as imageUri, " +
            "COALESCE(CommunityInteractions.isJoined, 0) as isJoined " +
            "from Community_Table left join (select * from Community_Interactions_Table where userId = :userId) as CommunityInteractions on Community_Table.communityId == CommunityInteractions.communityId " +
            "WHERE Community_Table.communityId = :communityId")
    fun getCommunityById(communityId: Int, userId: Int): LiveData<CommunityModel>

    @Query("DELETE FROM Community_Interactions_Table WHERE communityId = :communityId AND userId = :userId")
    suspend fun deleteCommunityInteractions(communityId: Int, userId: Int)
    @Query("UPDATE Community_Table SET noOfPosts = noOfPosts+1 WHERE communityId = :communityId")
    suspend fun increasePostCount(communityId: Int)
    @Query("UPDATE Community_Table SET noOfPosts = noOfPosts-1 WHERE communityId = :communityId")
    suspend fun decreasePostCount(communityId: Int)
    @Query("UPDATE Community_Table SET noOfMembers = noOfMembers+1 WHERE communityId = :communityId")
    suspend fun increaseMemberCount(communityId: Int)
    @Query("UPDATE Community_Table SET noOfMembers = noOfMembers-1 WHERE communityId = :communityId")
    suspend fun decreaseMemberCount(communityId: Int)

    @Query("select Community_Table.communityId, communityName, description, noOfMembers, noOfPosts, " +
            "contains_image as isCustomImage, image_uri as imageUri, " +
            "COALESCE(CommunityInteractions.isJoined, 0) as isJoined " +
            "from Community_Table left join (select * from Community_Interactions_Table where userId = :userId) as CommunityInteractions on Community_Table.communityId == CommunityInteractions.communityId")
    fun getCommunities(userId: Int): LiveData<List<CommunityModel>>

    @Query("select Community_Table.communityId, communityName, description, noOfMembers, noOfPosts, " +
            "contains_image as isCustomImage, image_uri as imageUri, " +
            "COALESCE(CommunityInteractions.isJoined, 0) as isJoined " +
            "from Community_Table left join (select * from Community_Interactions_Table where userId = :userId) as CommunityInteractions on Community_Table.communityId == CommunityInteractions.communityId " +
            "WHERE communityName LIKE '%' || :searchQuery || '%'")
    fun getCommunitiesMatchingQuery(searchQuery: String, userId: Int): LiveData<List<CommunityModel>>

    @Query("select Community_Table.communityId, communityName, description, noOfMembers, noOfPosts, " +
            "contains_image as isCustomImage, image_uri as imageUri, " +
            "COALESCE(CommunityInteractions.isJoined, 0) as isJoined " +
            "from Community_Table left join (select * from Community_Interactions_Table where userId = :userId) as CommunityInteractions on Community_Table.communityId == CommunityInteractions.communityId " +
            "WHERE CommunityInteractions.isJoined = 1")
    fun getJoinedCommunities(userId: Int): LiveData<List<CommunityModel>>

    @Query("select postId, title, containsImage, isCustomImage, imageUri, bodyText, voteCounter, " +
            "voteStatus, Posts.communityId, posterId " +
            "from (select Post_Table.postId, title, containsImage, isCustomImage, image_uri as imageUri, bodyText,voteCounter," +
            " COALESCE(PostInteractions.voteStatus, 'NONE') as voteStatus, " +
            "Post_Table.communityId, posterId from Post_Table left join (select * from Post_Interactions_Table where userId = :userId) as PostInteractions on Post_Table.postId == PostInteractions.postId) as Posts " +
            "" +
            "inner join " +
            "" +
            "(select Community_Table.communityId " +
            "from Community_Table left join (select * from Community_Interactions_Table where userId = :userId) as CommunityInteractions " +
            "" +
            "on Community_Table.communityId == CommunityInteractions.communityId " +
            "WHERE CommunityInteractions.isJoined = 1) as JoinedCommunities on Posts.communityId = JoinedCommunities.communityId")
    fun getPostsOfJoinedCommunities(userId: Int) : LiveData<List<PostModel>>

    //****** UPDATING OF POST AND COMMUNITY ******//
    @Query("UPDATE Post_Table SET bodyText = :bodyText WHERE postId = :postId")
    suspend fun updatePost(postId: Int, bodyText: String)
    @Query("UPDATE Community_Table SET description = :description WHERE communityId = :communityId")
    suspend fun updateCommunity(communityId: Int, description: String)

    @Query("select Comments_Table.commentId, commentText, imageUri, voteCounter, " +
            "COALESCE(CommentInteractions.voteStatus, 'NONE') as voteStatus, " +
            "postId, communityId, posterId from Comments_Table left join (select * from Comment_Interactions_Table where userId = :userId) as CommentInteractions on Comments_Table.commentId == CommentInteractions.commentId " +
            "WHERE posterId = :userId")
    fun getCommentsOfUser(userId: Int): LiveData<List<CommentModel>>

    @Query("select Post_Table.postId, title, containsImage, isCustomImage, image_uri as imageUri, bodyText,voteCounter," +
            " COALESCE(PostInteractions.voteStatus, 'NONE') as voteStatus, " +
            "communityId, posterId from Post_Table left join (select * from Post_Interactions_Table where userId = :userId) as PostInteractions on Post_Table.postId == PostInteractions.postId " +
            "where posterId = :userId")
    fun getPostsOfUser(userId: Int): LiveData<List<PostModel>>

    @Query("select Communities.communityId, Communities.communityName, Communities.description, Communities.noOfMembers, Communities.noOfPosts, " +
            "Communities.isCustomImage, Communities.imageUri, Communities.isJoined " +
            "from " +
            "(select Community_Table.communityId, communityName, description, noOfMembers, noOfPosts, " +
            "contains_image as isCustomImage, image_uri as imageUri, " +
            "COALESCE(CommunityInteractions.isJoined, 0) as isJoined " +
            "from Community_Table left join (select * from Community_Interactions_Table where userId = :userId) as CommunityInteractions on Community_Table.communityId == CommunityInteractions.communityId) as Communities " +
            "" +
            "inner join" +
            "" +
            "(select Post_Table.communityId, posterId from Post_Table left join (select * from Post_Interactions_Table where userId = :userId) as PostInteractions " +
            "on Post_Table.postId == PostInteractions.postId where posterId = :userId) as PostsOfUser " +
            "on Communities.communityId = PostsOfUser.communityId")
    fun getCommunityListForPostsOfUser(userId: Int): LiveData<List<CommunityModel>>
}

