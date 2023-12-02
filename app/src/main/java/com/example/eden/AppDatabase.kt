package com.example.eden

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.entities.relations.PostCommunityCrossRef


@Database(
    entities = [
        Post :: class,
        Community::class,
        PostCommunityCrossRef::class
    ],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun edenDao(): EdenDao
//    abstract fun postDao() : PostDao
//    abstract fun communityDao() : CommunityDao

    companion object{

        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context : Context): AppDatabase{
            synchronized(this){
                val tempInstance = INSTANCE
                if (tempInstance != null){
                    return tempInstance
                }

                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                    return instance
            }
        }
    }
}