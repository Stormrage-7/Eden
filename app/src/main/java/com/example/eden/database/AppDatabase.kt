package com.example.eden.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.JoinedCommunities
import com.example.eden.entities.Post
import com.example.eden.entities.relations.PostCommunityCrossRef
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Database(
    entities = [
        Post :: class,
        Community::class,
        PostCommunityCrossRef::class,
        JoinedCommunities :: class,
        Comment :: class
    ],
    version = 20
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun edenDao(): EdenDao

    companion object{
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context : Context): AppDatabase {
            synchronized(this){
                if (INSTANCE != null){
                    return INSTANCE as AppDatabase
                }

                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext as Eden,
                        AppDatabase::class.java,
                        "app_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    GlobalScope.launch {
                        INSTANCE!!.let {
//                            it.edenDao().deleteAllCommunities()
                            if(it.edenDao().getCommunityCount()==0){
                            it.edenDao().upsertCommunity(Community(0, "PS5", "Description 1", 10, false, imageUri = R.drawable.playstation_logo.toString()))
                            it.edenDao().upsertCommunity(Community(0, "Xbox", "Description 2", 25, false, imageUri = R.drawable.xbox_logo.toString()))
                            it.edenDao().upsertCommunity(Community(0, "Android", "Description 3", 100, false, imageUri = R.drawable.android_logo.toString()))
                            it.edenDao().upsertCommunity(Community(0, "IOS", "Description 4", 500, false, imageUri = R.drawable.icon_logo.toString()))
                            it.edenDao().upsertCommunity(Community(0, "PCMasterRace", "Description 5", 3, false, imageUri = R.drawable.icon_logo.toString()))
                            }
                        }
                    }
                    return INSTANCE as AppDatabase
            }
        }
    }
}