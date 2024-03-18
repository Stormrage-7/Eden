package com.example.eden.entities.relations

import androidx.room.Entity

@Entity(tableName = "Community_Interactions_Table", primaryKeys = ["userId", "communityId"])
data class CommunityInteractions(
    val userId: Int,
    val communityId: Int,
    val isJoined: Boolean
)