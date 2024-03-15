package com.example.eden.models

import java.io.Serializable

data class CommunityModel(
    val communityId: Int,
    val communityName: String,
    val description: String,
    val noOfMembers: Int = 0,
    val noOfPosts: Int = 0,
    val isCustomImage: Boolean,
    var imageUri: String,
    var isJoined: Boolean = false
): Serializable