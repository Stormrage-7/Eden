package com.example.eden.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eden.R
import com.example.eden.enums.Countries
import java.util.Date

@Entity(tableName = "User_Table")
data class User(
    @PrimaryKey (autoGenerate = true)
    val userId: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val mobileNo: String,
    val dob: Date,
    val country: Countries,
    val isCustomImage: Boolean,
    val profileImageUri: String
)