package com.example.eden.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.relations.JoinedCommunities
import com.example.eden.entities.Post
import com.example.eden.entities.User
import com.example.eden.entities.convertors.Converters
import com.example.eden.entities.ImageUri
import com.example.eden.entities.relations.CommentInteractions
import com.example.eden.entities.relations.CommunityInteractions
import com.example.eden.entities.relations.PostCommunityCrossRef
import com.example.eden.entities.relations.PostInteractions
import com.example.eden.enums.Countries
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date


@Database(
    entities = [
        Post :: class,
        Community::class,
        PostCommunityCrossRef::class,
        JoinedCommunities :: class,
        Comment :: class,
        User :: class,
        ImageUri::class,
        PostInteractions::class,
        CommentInteractions::class,
        CommunityInteractions::class
    ],
    version = 78)
@TypeConverters(Converters::class)

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
                        INSTANCE?.let {
//                            it.edenDao().deleteAllCommunities()
                            addData(it)
                            }
                        }
                    }
                    return INSTANCE as AppDatabase
            }
        }
    }

    private suspend fun addData(it: AppDatabase){
        if(it.edenDao().getCommunityCount()==0){
            //**** COMMUNITIES ****//
            it.edenDao().upsertCommunity(Community(0, "Nature", "We are here to appreciate the awesome majesty and incredibly cool aspects of nature. \uD83D\uDD25",
                3, isCustomImage = false, imageUri = R.drawable.nature_logo.toString()))
            it.edenDao().upsertCommunity(Community(0, "Playstation", "Your community-run home for all things PlayStation on Reddit! Console/game discussions, news, support, trophy/media sharing and more!",
                10, isCustomImage = false, imageUri = R.drawable.playstation_logo.toString()))
            it.edenDao().upsertCommunity(Community(0, "Android", "Android news, reviews, tips, and discussions about rooting, tutorials, and apps. General discussion about devices is welcome.",
                100, isCustomImage = false, imageUri = R.drawable.android_logo.toString()))
            it.edenDao().upsertCommunity(Community(0, "Xbox", "Topics related to all versions of the Xbox video game consoles, games, online services, controllers, etc.",
                25, isCustomImage = false, imageUri = R.drawable.xbox_logo.toString()))
            it.edenDao().upsertCommunity(Community(0, "IOS", "iOS - Developed by Apple Inc.",
                500, isCustomImage = false, imageUri = R.drawable.ios_logo.toString()))
            it.edenDao().upsertCommunity(Community(0, "PC-MasterRace", "PC Master Race - PCMR: A place where all enthusiasts of PC, PC gaming and PC technology are welcome!",
                144, isCustomImage = false, imageUri = R.drawable.pcmasterrace_logo.toString()))

            //**** POSTS ****//
            //1
            it.edenDao().upsertPost(
                Post(0, "A little lion snarling at my remote camera", containsImage = true, isCustomImage = false, imageUri = R.drawable.lion_snarl.toString(),
                bodyText = "", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 1, posterId = 1)
            )

            //2
            it.edenDao().upsertPost(
                Post(0, "Touching North America and Europe at the same time", containsImage = true, isCustomImage = false, imageUri = R.drawable.na_and_eu.toString(),
                bodyText = "", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 1, posterId = 1)
            )

            //3
            it.edenDao().upsertPost(
                Post(0, "Shizuoka, Japan", containsImage = true, isCustomImage = false, imageUri = R.drawable.shizuoka_japan.toString(),
                bodyText = "", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 1, posterId = 1)
            )

            for (i in 1 .. 3) {it.edenDao().increasePostCount(1)}

            //4
            it.edenDao().upsertPost(
                Post(0, "Spider-Man: The Great Web Trailer Leaked Online", containsImage = true, isCustomImage = false, imageUri = R.drawable.spider_man.toString(),
                bodyText = "", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 2, posterId = 1)
            )

            //5
            it.edenDao().upsertPost(
                Post(0, "What I never understood: yes Kojima was forced to quit the project but why not starting a new one with new name? Based on PT", containsImage = true, isCustomImage = false, imageUri = R.drawable.kojima_pt.toString(),
                bodyText = "The demo had barely something to do with the old silent hill games. It could have been something else.", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 2, posterId = 1)
            )

            for (i in 1 .. 2) {it.edenDao().increasePostCount(2)}

            // ANDROID
            //6
            it.edenDao().upsertPost(
                Post(0, "Xiaomi 14 Pro is an extremely underrated phone.", containsImage = false, isCustomImage = false, imageUri = "",
                bodyText = "I sell phones as a little side hustle and get to try all the top Flagships in the United States. So far I have used the S24 Ultra, iPhone 15 pro Max and the Google Pixel 8 Pro. I recently purchased a Xiaomi 14 Pro running the Xiaomi EU Rom and I was blown away! I actually preferred it over the S24 Ultra and decided to sell it. It's Display was on par with all 3 of those phones if not better. The software was actually more smooth than all the other phones which surprised me. You can purchase them brand new for around 700\$ and flash the EU Room to make it run like a global rom.\n" +
                        "\n" +
                        "The only negative I would say are the cameras, they are good but not on par with the other 3 phones but you can still get some really good shots on them.", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 3, posterId = 1)
            )

            //7
            it.edenDao().upsertPost(
                Post(0, "Nothing Phone (2a) is official with Dimensity 7200 Pro, Glyph design", containsImage = true, isCustomImage = false, imageUri = R.drawable.nothing_phone_2a.toString(),
                bodyText = "https://9to5google.com/2024/03/05/nothing-phone-2a-price-specs-release-date/", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 3, posterId = 1)
            )

            //8
            it.edenDao().upsertPost(
                Post(0, "Movie tickets and boarding passes can now automatically appear in Google Wallet", containsImage = false, isCustomImage = false, imageUri = "",
                bodyText = "Google has announced that movie tickets and boarding passes will automatically be added to Google Wallet when you get a confirmation email in Gmail! This feature is live for some global movie chains and airlines but more partners will be added in the future, according to the announcement.\n" +
                        "\n" +
                        "Furthermore, Google also says you can now manually archive passes in Wallet (on both mobile and Wear OS), which will move them down to the \"Archived Passes\" section.", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 3, posterId = 1)
            )

            //9
            it.edenDao().upsertPost(
                Post(0, "Meta Reportedly Rejected Google Partnership to Bring Android XR to Quest", containsImage = true, isCustomImage = false, imageUri = R.drawable.meta_google.toString(),
                bodyText = "", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 3, posterId = 1)
            )

            for (i in 1 .. 4) {it.edenDao().increasePostCount(3)}

            // PC MASTERRACE
            //10
            it.edenDao().upsertPost(
                Post(0, "'Nvidia displays their entire stock of 3080s' - Sept 1, 2020", containsImage = true, isCustomImage = false, imageUri = R.drawable.nvidia_3080.toString(),
                bodyText = "", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 6, posterId = 1)
            )

            //11
            it.edenDao().upsertPost(
                Post(0, "Guys, my PC is running hot and I can't put a finger on it?", containsImage = true, isCustomImage = false, imageUri = R.drawable.pc_running_hot.toString(),
                bodyText = "", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 6, posterId = 1)
            )

            //12
            it.edenDao().upsertPost(
                Post(0, "Should I sell my 3070 to buy 4060ti 16gb ?", containsImage = false, isCustomImage = false, imageUri = "",
                bodyText = "If I’m able to sell my 3070 and spend that money to upgrade to 4060 ti 16gb . Would that be smart move ? It has 16vram and 4000 series features DLSS3.. Plus it uses less power\n" +
                        "\n" +
                        "** i might spend 70-100 \$ Different thats all should i do it ?", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 6, posterId = 1)
            )

            //13
            it.edenDao().upsertPost(
                Post(0, "Dual monitor setups be like", containsImage = true, isCustomImage = false, imageUri = R.drawable.dual_monitor.toString(),
                bodyText = "", voteCounter = (0..25).random(), dateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                communityId = 6, posterId = 1)
            )

            for (i in 1 .. 4) {it.edenDao().increasePostCount(6)}

            //**** COMMENTS ****//

            it.edenDao().upsertComment(
                Comment(0, commentText = "Don't forget the negative of missing 4G and 5G bands.\n" +
                            "\n" +
                            "Quite important in a phone!", postId = 6, communityId = 3, posterId = 1, postTitle = "Xiaomi 14 Pro is an extremely underrated phone.", voteCounter = (0..25).random()))

            it.edenDao().upsertComment(
                Comment(0, commentText = "More like one new monitor and one older and smaller monitor from your previous build.",
                    postId = 13, communityId = 6, posterId = 1, postTitle = "Dual monitor setups be like", voteCounter = (0..25).random()))

            it.edenDao().upsertComment(
                Comment(0, commentText = "I'm actually kinda the opposite. One monitor is high refresh rate, but not the best color accuracy. The other one is a standard 60hz, but best image quality I could buy at the price.",
                    postId = 13, communityId = 6, posterId = 3, postTitle = "Dual monitor setups be like", voteCounter = (0..25).random()))

            it.edenDao().upsertComment(
                Comment(0, commentText = "My 2nd is a TV", postId = 13, communityId = 6, posterId = 2, postTitle = "Dual monitor setups be like", voteCounter = (0..25).random()))

            it.edenDao().upsertComment(
                Comment(0, commentText = "Mount fuji has truly inspired a million photographs",
                    postId = 3, communityId = 1, posterId = 3, postTitle = "Shizuoka, Japan", voteCounter = (0..25).random()))

            it.edenDao().upsertComment(
                Comment(0, commentText = "One day I’ll be able to stand for a minute under the most perfect Sakura. I imagine it’ll look just like this.",
                    postId = 3, communityId = 1, posterId = 1, postTitle = "Shizuoka, Japan", voteCounter = (0..25).random()))

            it.edenDao().upsertComment(
                Comment(0, commentText = "When does cherry tree blossom? If i want to visit Japan? I wanna coordinate my visit.",
                    postId = 3, communityId = 1, posterId = 2, postTitle = "Shizuoka, Japan", voteCounter = (0..25).random()))

            it.edenDao().upsertComment(
                Comment(0, commentText = "I'm currently living in the suburbs of Tokyo and there are some spots where they have already started blooming, but most don't start to bloom until mid-March to early April.\n" +
                        "\n" +
                        "You can check this site for when you plan your trip. They give good estimates on when they start to bloom in most places around Japan :)\n" +
                        "\n" +
                        "https://www.japan-guide.com/sakura/",
                    postId = 3, communityId = 1, posterId = 1, postTitle = "Shizuoka, Japan", voteCounter = (0..25).random()))

            it.edenDao().upsertComment(
                Comment(0, commentText = "This is the coolest picture of a cub I have ever seen.", postId = 1, communityId = 1, posterId = 1, postTitle = "A little lion snarling at my remote camera", voteCounter = (0..25).random()))

            //**** USER ****//
            it.edenDao().upsertUser(User(0, "u/Sharan451", "Sharan", "Kumar", "sharankumar157@gmail.com", "7845845617", Date(), Countries.NONE, false, R.drawable.ic_avatar_profile_1.toString()))
            it.edenDao().upsertUser(User(0, "u/Jane_Doe123", "Jane", "Doe", "janedoe@gmail.com", "7845845617", Date(), Countries.NONE, false, R.drawable.ic_avatar_profile_2.toString()))
            it.edenDao().upsertUser(User(0, "u/RandomUser", "Anonymous", "", "anonymous@gmail.com", "7845845617", Date(), Countries.NONE, false, R.drawable.ic_avatar_profile_3.toString()))
        }
}