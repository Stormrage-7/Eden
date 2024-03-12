package com.example.eden.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eden.enums.VoteStatus
import java.io.Serializable

@Entity(tableName = "Comments_Table")
data class Comment (
    @PrimaryKey(autoGenerate = true)
    val commentId: Int,
    val posterName: String = "Sharan",
    val text: String,
    val imageUri: String? = null,
    var voteCounter: Int = 0,
    var voteStatus: VoteStatus = VoteStatus.NONE,
    val postId: Int,
    val communityId: Int
): Serializable