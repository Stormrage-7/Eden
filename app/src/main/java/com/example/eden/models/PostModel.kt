package com.example.eden.models

import com.example.eden.enums.VoteStatus
import java.io.Serializable

data class PostModel (
    val postId: Int = 0,
    val title: String,
    val containsImage: Boolean,
    val isCustomImage: Boolean, //This attribute is only here to facilitate addition of static data. It won't be a part of the actual app workflow.
    val imageUri: String,
    val bodyText: String,
    var voteCounter: Int = 0,
    var voteStatus: VoteStatus = VoteStatus.NONE,
    val communityId: Int,
    val posterId: Int  // UserID
) : Serializable