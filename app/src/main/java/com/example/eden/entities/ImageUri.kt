package com.example.eden.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "image_uri_table")
data class ImageUri(
    @PrimaryKey (autoGenerate = true)
    val id: Int = 0,
    val uri: String
)