package com.example.eden.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eden.enums.VoteStatus
import java.io.Serializable

@Entity(tableName = "Post_Table")
data class Post (
    @PrimaryKey(autoGenerate = true)
    val postId: Int = 0,
    val title: String,
    val containsImage: Boolean,

    //This attribute is only here to facilitate addition of static data. It won't be a part of the actual app workflow.
    val isCustomImage: Boolean,

    @ColumnInfo(name = "image_uri")
    val imageUri: String,
    val bodyText: String,
    var voteCounter: Int = 0,
    var voteStatus: VoteStatus = VoteStatus.NONE,
    val dateTime: Long,
    val communityId: Int,
    val posterId: Int  // UserID
    ) : Serializable
//    val isPinned: Boolean = false,
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
