package com.example.eden.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.eden.entities.Community
import com.example.eden.entities.Post

data class PostWithCommunities(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "postId",
        entityColumn = "communityId",
        associateBy = Junction(PostCommunityCrossRef::class)
    )
    val communities: Community
)
