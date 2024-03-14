package com.example.eden.models

import com.example.eden.enums.VoteStatus
import java.io.Serializable

data class Comment (
    val commentId: Int,
    val text: String,
    val imageUri: String? = null,
    var voteCounter: Int = 0,
    var voteStatus: VoteStatus = VoteStatus.NONE,
    val postId: Int,
    val communityId: Int,
    val posterId: Int
): Serializable