package com.example.eden.entities.relations

import androidx.room.Entity

@Entity(primaryKeys = ["postId", "communityId"])
data class PostCommunityCrossRef(
    val postId: Int,
    val communityId: Int
)