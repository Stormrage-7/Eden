package com.example.eden

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Comments_Table")
data class Comment (
    @PrimaryKey(autoGenerate = true)
    val commentId: Int,
    val posterName: String = "Sharan",
    val text: String,
    val postId: Int
){
}