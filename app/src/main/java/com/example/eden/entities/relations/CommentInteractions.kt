package com.example.eden.entities.relations

import androidx.room.Entity
import com.example.eden.enums.VoteStatus

@Entity (tableName = "Comment_Interactions_Table")
data class CommentInteractions(
    val userId: Int,
    val commentId: Int,
    val voteStatus: VoteStatus
)