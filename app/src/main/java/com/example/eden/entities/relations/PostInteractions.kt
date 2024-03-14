package com.example.eden.entities.relations

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eden.enums.VoteStatus

@Entity(tableName = "Post_Interactions_Table", primaryKeys = ["userId", "postId"])
data class PostInteractions (
//    @PrimaryKey(autoGenerate = true)
//    val id: Int,
    val userId: Int,
    val postId: Int,
    val voteStatus: VoteStatus
)