package com.example.eden.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.eden.Eden
import com.example.eden.database.EdenDao
import com.example.eden.R
import com.example.eden.databinding.ActivityHomeScreenBinding
import com.example.eden.dialogs.ConfirmationDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeScreenActivity: AppCompatActivity(){
    private lateinit var activityHomeScreenBinding: ActivityHomeScreenBinding
    private lateinit var navController : NavController
    private lateinit var databaseDao: EdenDao
    private lateinit var current: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        databaseDao = (application as Eden).edenDao

        activityHomeScreenBinding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(activityHomeScreenBinding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        val homeFragment = HomeFragment()
        val communitiesFragment = CommunitiesFragment()
        val yourFeedFragment = CustomFeedFragment()
        current = homeFragment
//        supportFragmentManager.beginTransaction().add(activityHomeScreenBinding.navHostFragment.id, communitiesFragment, "3").hide(communitiesFragment).commit()
//        supportFragmentManager.beginTransaction().add(activityHomeScreenBinding.navHostFragment.id, yourFeedFragment, "2").hide(yourFeedFragment).commit()
//        supportFragmentManager.beginTransaction().add(activityHomeScreenBinding.navHostFragment.id, homeFragment, "1").commit()
        setCurrentFragment(homeFragment)

        setSupportActionBar(activityHomeScreenBinding.topAppBar)
        activityHomeScreenBinding.homeScreenSearchView.setupWithSearchBar(activityHomeScreenBinding.searchBar)
        activityHomeScreenBinding.searchBar.setNavigationOnClickListener {
            activityHomeScreenBinding.drawerLayout.open()
        }

        activityHomeScreenBinding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId){
                R.id.add_community_btn -> {
                    Intent(this, NewCommunityActivity::class.java).apply {
                        startActivity(this)
                    }
                }
                R.id.post_interactions_btn -> {
                    Intent(this, PostInteractionsActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            }
            activityHomeScreenBinding.drawerLayout.close()
            false
        }

        activityHomeScreenBinding.homeScreenSearchView.editText.setOnEditorActionListener { v, actionId, event ->
            val query = activityHomeScreenBinding.homeScreenSearchView.text.toString().trim()
            if (query.isNotEmpty() and query.startsWith("https://www.eden.com")){
                Intent(this@HomeScreenActivity, PostDetailedActivity::class.java).apply {
                    putExtra("UriObject", query)
                    startActivity(this)
                }
            }
            else if (query.isNotEmpty()) {
                Intent(this@HomeScreenActivity, SearchableActivity::class.java).apply {
                    putExtra(SearchManager.QUERY, query)
                    startActivity(this)
                }
            }
            activityHomeScreenBinding.homeScreenSearchView.hide()
            false
        }
//        activityHomeScreenBinding.homeScreenSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
////                activityHomeScreenBinding.homeScreenSearchView.setQuery("", false)
////                activityHomeScreenBinding.homeScreenSearchView.clearFocus()
////                activityHomeScreenBinding.topAppBar.requestFocus()
//                if (query?.startsWith("https://www.eden.com") == true){
//                    Intent(this@HomeScreenActivity, PostDetailedActivity::class.java).apply {
//                        putExtra("UriObject", query)
//                        startActivity(this)
//                    }
//                }
//                else {
//                    Intent(this@HomeScreenActivity, SearchableActivity::class.java).apply {
//                        putExtra(SearchManager.QUERY, query)
//                        startActivity(this)
//                    }
//                }
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//
//        })

        activityHomeScreenBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.miHome -> {
                    setCurrentFragment(homeFragment)
                    current = homeFragment
                    true
                }
                R.id.miCreatePost -> {
                    Intent(this, NewPostActivity::class.java).apply {
                        startActivity(this)
                    }
                    false
                }
                R.id.miCustomFeed -> {
                    setCurrentFragment(yourFeedFragment)
                    current = yourFeedFragment
                    true
                }
                R.id.miCommunities -> {
                    setCurrentFragment(communitiesFragment)
                    current = communitiesFragment
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.i("This is a log message!")
    }

    override fun onResume() {
        super.onResume()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (activityHomeScreenBinding.drawerLayout.isOpen){
            activityHomeScreenBinding.drawerLayout.close()
            return
        }
        else {
            super.onBackPressed()
//            activityHomeScreenBinding.bottomNavigationView.selectedItemId = when(Navigation.findNavController(this,
//                R.id.nav_host_fragment
//            ).currentDestination!!.id){
//                R.id.homeFragment -> R.id.miHome
//                R.id.communitiesFragment -> R.id.miCommunities
//                R.id.customFeedFragment -> R.id.miCustomFeed
//                else -> {0}
//            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
//        supportFragmentManager.beginTransaction().apply {
//            hide(current)
//            show(fragment)
//            commit()
//        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment, fragment)
            commit()
        }

    private fun isValidDestination(destination: Int) : Boolean{
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment).currentDestination!!.id
    }

}