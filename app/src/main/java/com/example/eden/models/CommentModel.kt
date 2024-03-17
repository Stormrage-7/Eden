package com.example.eden.models

import com.example.eden.enums.VoteStatus
import java.io.Serializable

data class CommentModel (
    val commentId: Int,
    val commentText: String,
    val imageUri: String? = null,
    var voteCounter: Int = 0,
    var voteStatus: VoteStatus = VoteStatus.NONE,
    val postId: Int,
    val postTitle: String,
    val communityId: Int,
    val posterId: Int
): Serializable