package com.example.eden

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.eden.databinding.ActivityHomeScreenBinding
import com.example.eden.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeScreenActivity: AppCompatActivity() {
    private lateinit var activityHomeScreenBinding: ActivityHomeScreenBinding
    private lateinit var navController : NavController
    private lateinit var databaseDao: EdenDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        databaseDao = (application as Eden).edenDao
//        lifecycleScope.launch {
//            clearDB()
//        }

        activityHomeScreenBinding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(activityHomeScreenBinding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        setSupportActionBar(findViewById(R.id.CustomToolBar))

        activityHomeScreenBinding.topAppBar.addCommunityBtn.setOnClickListener {
            activityHomeScreenBinding.topAppBar.homeScreenSearchView.isIconified = true
            Intent(this, NewCommunityActivity::class.java).apply {
                startActivity(this)
            }
        }

        activityHomeScreenBinding.topAppBar.homeScreenSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                activityHomeScreenBinding.topAppBar.homeScreenSearchView.setQuery("", false)
                activityHomeScreenBinding.topAppBar.homeScreenSearchView.clearFocus()
                activityHomeScreenBinding.topAppBar.homeScreenSearchView.isIconified = true
                Intent(this@HomeScreenActivity, SearchableActivity:: class.java).apply {
                    putExtra(SearchManager.QUERY, query)
                    startActivity(this)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        activityHomeScreenBinding.bottomNavigationView.setOnItemSelectedListener{
            activityHomeScreenBinding.topAppBar.homeScreenSearchView.isIconified = true
            when(it.itemId){
                R.id.miHome -> {

                    if( isValidDestination(R.id.homeFragment) and !navController.popBackStack(R.id.homeFragment, false)) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                    //TODO Change to Nested If so that if HOME is pressed again then scroll back to top!

                }
                R.id.miCommunities -> {

                    if( isValidDestination(R.id.communitiesFragment) and !navController.popBackStack(R.id.communitiesFragment, false)) {
                        navController.navigate(R.id.communitiesFragment)
                    }
                    true
                }
                R.id.miCreatePost -> {
                    Intent(this, NewPostActivity::class.java).apply {
                        startActivity(this)
                    }
                    false
                }
                R.id.miCustomFeed -> {
                    if( isValidDestination(R.id.customFeedFragment) and !navController.popBackStack(R.id.customFeedFragment, false)) {
                        navController.navigate(R.id.customFeedFragment)
                    }
                    true
                }
                else -> {false}
            }

        }
    }

    override fun onStart() {
        super.onStart()
        Timber.i("This is a log message!")
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.appbar_menu, menu)
//        return true
//    }

    override fun onBackPressed() {
        if (!activityHomeScreenBinding.topAppBar.homeScreenSearchView.isIconified){
            activityHomeScreenBinding.topAppBar.homeScreenSearchView.isIconified = true
        }
        else {
            super.onBackPressed()
            activityHomeScreenBinding.bottomNavigationView.selectedItemId = when(Navigation.findNavController(this, R.id.nav_host_fragment).currentDestination!!.id){
                R.id.homeFragment -> R.id.miHome
                R.id.communitiesFragment -> R.id.miCommunities
                R.id.customFeedFragment -> R.id.miCustomFeed
//            R.id.inboxFragment -> R.id.miInbox
                else -> {0}
            }
        }
    }

    private fun isValidDestination(destination: Int) : Boolean{
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment).currentDestination!!.id
    }

    private suspend fun clearDB(){
        withContext(Dispatchers.IO) {
            databaseDao.deleteAllPosts()
        }
    }

}