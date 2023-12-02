package com.example.eden

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import androidx.room.Query
import com.example.eden.entities.Post


@Dao
interface EdenDao {

    @Query("SELECT * FROM Post_Table")
    fun getAll(): LiveData<List<Post>>

    @Upsert
    suspend fun upsertPost(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM Post_Table")
    fun deleteAll(): Int
}