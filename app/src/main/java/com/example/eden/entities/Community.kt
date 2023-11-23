package com.example.eden.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eden.R

@Entity(tableName = "Community_Table")
class Community(
    @PrimaryKey(autoGenerate = true)
    val communityId: Int = 0,
    @ColumnInfo
    val communityName: String,
    @ColumnInfo
    val description: String,
    @ColumnInfo
    val noOfMembers: Int,
    @ColumnInfo(name = "contains_image")
    val containsImage: Boolean = false,
    @ColumnInfo(name = "image_uri")
    var imageUri: String = "",
    @ColumnInfo(name = "image_src")
    var imageSrc: Int = 0,
    var isJoined: Boolean = false
//    val membersList: MutableList<Int>, // Should go in helper class which will act as crossreference between two classes (Post and Subreddit)
//    val subredditType: SubredditType,
//    val flairs: MutableList<String>,
//    val moderators: MutableList<Int>,  //User IDs
//    val rules: HashMap<Int, String>,
//    val avatar: Serializable
) {
    companion object{
        var communityList: List<Community> = listOf<Community>(
            Community(0, "PS5", "Description 1", 10, true, imageSrc = R.drawable.playstation_logo),
            Community(0, "Xbox", "Description 2", 25, true, imageSrc = R.drawable.xbox_logo),
            Community(0, "Test 3", "Description 3", 100, false),
            Community(0, "Test 4", "Description 4", 500, false),
            Community(0, "Test 5", "Description 5", 3, false),
            Community(0, "Test 6", "Description 6", 90, false),
            Community(0, "Test 7", "Description 7", 780, false),
            Community(0, "Test 8", "Description 8", 10000, false),
            Community(0, "Test 9", "Description 9", 5623, false),
            Community(0, "Test 10", "Description 10", 2400, false),
            Community(0, "Test 11", "Description 11", 4501, false),
        )
    }
}