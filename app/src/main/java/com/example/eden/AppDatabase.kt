package com.example.eden

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
        JoinedCommunities :: class
    ],
    version = 15
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
//                val tempInstance = INSTANCE
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
//                    INSTANCE = instance
                    GlobalScope.launch {
                        INSTANCE!!.let {
//                            it.edenDao().deleteAllCommunities()
                            if(it.edenDao().getCommunityCount()==0){
                            it.edenDao().upsertCommunity(Community(0, "PS5", "Description 1", 10, true, imageSrc = R.drawable.playstation_logo))
                            it.edenDao().upsertCommunity(Community(0, "Xbox", "Description 2", 25, true, imageSrc = R.drawable.xbox_logo))
                            it.edenDao().upsertCommunity(Community(0, "Android", "Description 3", 100, true, imageSrc = R.drawable.android_logo))
                            it.edenDao().upsertCommunity(Community(0, "IOS", "Description 4", 500, true, imageSrc = R.drawable.icon_logo))
                            it.edenDao().upsertCommunity(Community(0, "PCMasterRace", "Description 5", 3, true, imageSrc = R.drawable.icon_logo))
                            }
//                            it.edenDao().upsertCommunity(Community(0, "Science", "Description 6", 90, false))
//                            it.edenDao().upsertCommunity(Community(0, "Test 7", "Description 7", 780, false))
//                            it.edenDao().upsertCommunity(Community(0, "Test 8", "Description 8", 10000, false))
//                            it.edenDao().upsertCommunity(Community(0, "Test 9", "Description 9", 5623, false))
//                            it.edenDao().upsertCommunity(Community(0, "Test 10", "Description 10", 2400, false))
//                            it.edenDao().upsertCommunity(Community(0, "Test 11", "Description 11", 4501, false))
                        }

                    }
                    return INSTANCE as AppDatabase
            }
        }
    }
}