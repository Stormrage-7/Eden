package com.example.eden

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Subreddit_Table")
class Subreddit(
    @PrimaryKey
    val subredditId: Int,
    @ColumnInfo
    val subredditName: String,
    @ColumnInfo
    val description: String,
    @ColumnInfo
    val noOfMembers: Int,
    @ColumnInfo
    val membersList: MutableList<Int>, // User IDs
    val subredditType: SubredditType,
    val flairs: MutableList<String>,
    val moderators: MutableList<Int>,  //User IDs
    val rules: HashMap<Int, String>,
    val avatar: Serializable
) {
}