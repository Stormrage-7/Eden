package com.example.eden

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PostDao {

    @Query("SELECT * FROM Post_Table")
    fun getAll(): List<Post>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(post: Post)

    @Delete
    fun delete(post: Post)

    @Query("DELETE FROM Post_Table")
    fun deleteAll(): Int
}