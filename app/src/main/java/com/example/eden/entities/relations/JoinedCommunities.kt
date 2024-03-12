package com.example.eden.entities.relations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Joined_Communities_Table")
data class JoinedCommunities(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val communityId: Int
)