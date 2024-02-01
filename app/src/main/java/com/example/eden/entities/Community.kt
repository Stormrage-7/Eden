package com.example.eden.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eden.R
import java.io.Serializable

@Entity(tableName = "Community_Table")
data class Community(
    @PrimaryKey(autoGenerate = true)
    val communityId: Int,
    val communityName: String,
    val description: String,
    val noOfMembers: Int = 0,
    val noOfPosts: Int = 0,
    @ColumnInfo(name = "contains_image")
    val isCustomImage: Boolean,
    @ColumnInfo(name = "image_uri")
    var imageUri: String,
    var isJoined: Boolean = false
//    val membersList: MutableList<Int>, // Should go in helper class which will act as crossreference between two classes (Post and Subreddit)
//    val subredditType: SubredditType,
//    val flairs: MutableList<String>,
//    val moderators: MutableList<Int>,  //User IDs
//    val rules: HashMap<Int, String>,
//    val avatar: Serializable
): Serializable