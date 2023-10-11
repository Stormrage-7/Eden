package com.example.eden

import java.io.Serializable

class Subreddit(
    val subredditId: Int,
    val name: String,
    val description: String,
    val noOfMembers: Int,
    val membersList: MutableList<Int>, // User IDs
    val subredditType: SubredditType,
    val flairs: MutableList<String>,
    val moderators: MutableList<Int>,  //User IDs
    val rules: HashMap<Int, String>,
    val avatar: Serializable
) {
}