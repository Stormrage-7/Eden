package com.example.eden.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Comments_Table")
data class Comment (
    @PrimaryKey(autoGenerate = true)
    val commentId: Int,
    val commentText: String,
    val imageUri: String? = null,
    var voteCounter: Int = 0,
    val postId: Int,
    val communityId: Int,
    val posterId: Int
)