package com.example.eden.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.eden.entities.Community
import com.example.eden.entities.Post

data class CommunityWithPosts(
    @Embedded val community: Community,
    @Relation(
        parentColumn = "communityId",
        entityColumn = "postId",
        associateBy = Junction(PostCommunityCrossRef::class)
    )
    val posts: List<Post>
)
