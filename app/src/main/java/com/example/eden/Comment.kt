package com.example.eden

import java.io.Serializable

class Comment (
    val commentId: Int,
    val commentPosterId: Int,
    val text: String,
    val replyComments: MutableList<Comment>,
    val url: String,
    val ImageOrGif: Serializable,
    val getReplyNotification: Boolean,
    val isCollapsed: Boolean,
    val isPinned: Boolean,
    val awardList: MutableList<Award>
){
}