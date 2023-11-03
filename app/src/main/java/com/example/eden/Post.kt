package com.example.eden

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eden.Enums.mediaType

@Entity(tableName = "Post_Table")
data class Post (
    @PrimaryKey(autoGenerate = true)
    val postId: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "contains_image")
    val containsImage: Boolean,
    @ColumnInfo(name = "image_uri")
    val imageUri: String,
    @ColumnInfo(name = "body_text")
    val bodyText: String,
    @ColumnInfo(name = "vote_counter")
    val voteCounter: Int = 0,
    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false
    )
//    val flairs: MutableList<String>,
//    val awardList: MutableList<Award> = mutableListOf(),
//    val commentSectionType: CommentSectionType = CommentSectionType.OPEN,
//    val comments: List<Comment>,
//    val commentFilter: CommentFilter = CommentFilter.TOP,
//    val postStatus: PostStatus = PostStatus.APPROVED,
//    val removalReason: String = "",
//    // TODO Move removal reason to Enum based on the community's rules
//    val isDistinguishedAsMod: Boolean = false,
//    val censorship: Censorship = Censorship.NONE,
//    val crowdControlSettings: CrowdControlSettings = CrowdControlSettings.OFF)
{
//    constructor(title: String, text: String, byteArray: ByteArray):this


}