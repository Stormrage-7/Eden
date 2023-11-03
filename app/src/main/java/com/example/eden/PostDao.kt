package com.example.eden

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Upsert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PostDao {

    @Query("SELECT * FROM Post_Table")
    fun getAll(): LiveData<List<Post>>

    @Upsert
    suspend fun upsertPost(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM Post_Table")
    fun deleteAll(): Int
}