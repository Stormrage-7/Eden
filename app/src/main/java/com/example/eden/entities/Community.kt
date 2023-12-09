package com.example.eden.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eden.R

@Entity(tableName = "Community_Table")
data class Community(
    @PrimaryKey(autoGenerate = true)
    val communityId: Int = 0,
    @ColumnInfo
    val communityName: String,
    @ColumnInfo
    val description: String,
    @ColumnInfo
    val noOfMembers: Int,
    @ColumnInfo(name = "contains_image")
    val isCustomImage: Boolean = false,
    @ColumnInfo(name = "image_uri")
    var imageUri: String = "",
//    @ColumnInfo(name = "image_src")
//    var imageSrc: Int = 0,
    var isJoined: Boolean = false
//    val membersList: MutableList<Int>, // Should go in helper class which will act as crossreference between two classes (Post and Subreddit)
//    val subredditType: SubredditType,
//    val flairs: MutableList<String>,
//    val moderators: MutableList<Int>,  //User IDs
//    val rules: HashMap<Int, String>,
//    val avatar: Serializable
) {
}