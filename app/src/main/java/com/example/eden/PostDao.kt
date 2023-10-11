package com.example.eden

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PostDao {

//    @Query("SELECT * FROM post_table")
//    fun getAll(): List<Post>
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insert(post: Post)
//
//    @Delete
//    suspend fun delete(post: Post)

//    @Query("DELETE FROM post_table")
//    suspend fun deleteAll()
}