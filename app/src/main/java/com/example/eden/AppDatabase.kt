package com.example.eden

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Post :: class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao() : PostDao

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