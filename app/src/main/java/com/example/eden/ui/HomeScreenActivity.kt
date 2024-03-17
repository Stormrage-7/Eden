package com.example.eden.ui

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.eden.Eden
import com.example.eden.database.EdenDao
import com.example.eden.R
import com.example.eden.database.AppRepository
import com.example.eden.databinding.ActivityHomeScreenBinding
import com.example.eden.ui.viewmodels.HomeViewModel
import com.example.eden.ui.viewmodels.HomeViewModelFactory
import com.example.eden.util.PostUriValidator
import com.example.eden.util.UriValidator
import timber.log.Timber

class HomeScreenActivity: AppCompatActivity(){
    private lateinit var activityHomeScreenBinding: ActivityHomeScreenBinding
    private lateinit var databaseDao: EdenDao
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: HomeViewModelFactory
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        databaseDao = (application as Eden).edenDao
        repository = (application as Eden).repository
        factory = HomeViewModelFactory(repository, this)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        activityHomeScreenBinding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(activityHomeScreenBinding.root)

        val headerView = activityHomeScreenBinding.navigationView.getHeaderView(0)
        val imageViewProfile = headerView.findViewById<ImageView>(R.id.imageViewNavDrawer)
        val textViewProfile = headerView.findViewById<TextView>(R.id.textViewNavDrawer)
        val btnChangeProfile = headerView.findViewById<Button>(R.id.changeProfileBtn)
        imageViewProfile.setOnClickListener { openProfile() }
        textViewProfile.setOnClickListener { openProfile() }
        btnChangeProfile.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menu)
        }


        viewModel.user.observe(this) { user ->
            if (user!=null) {
                if (!user.isCustomImage) imageViewProfile.setImageResource(user.profileImageUri.toInt())
                else {
                    if (UriValidator.validate(this, user.profileImageUri)) imageViewProfile.setImageURI(
                        Uri.parse(user.profileImageUri))
                    else imageViewProfile.setImageResource(R.drawable.ic_avatar)
                }
                textViewProfile.text = user.username
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.findNavController()

        // Setup the bottom navigation view with navController
//        activityHomeScreenBinding.bottomNavigationView.setupWithNavController(navController)


//        val homeFragment = HomeFragment()
//        val communitiesFragment = CommunitiesFragment()
//        val yourFeedFragment = CustomFeedFragment()
//        current = homeFragment
//        supportFragmentManager.beginTransaction().add(activityHomeScreenBinding.navHostFragment.id, communitiesFragment, "3").hide(communitiesFragment).commit()
//        supportFragmentManager.beginTransaction().add(activityHomeScreenBinding.navHostFragment.id, yourFeedFragment, "2").hide(yourFeedFragment).commit()
//        supportFragmentManager.beginTransaction().add(activityHomeScreenBinding.navHostFragment.id, homeFragment, "1").commit()
//        setCurrentFragment(homeFragment)

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
                R.id.bookmarks_Btn -> {
                    Intent(this, BookmarksActivity::class.java).apply {
                        startActivity(this)
                    }
                }
                R.id.profile_btn -> {
                    openProfile()
                }
            }
            activityHomeScreenBinding.drawerLayout.close()
            false
        }

        activityHomeScreenBinding.homeScreenSearchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = activityHomeScreenBinding.homeScreenSearchView.text.toString().trim()
            if (query.isNotEmpty() and PostUriValidator.validate(query)){
                Intent(this@HomeScreenActivity, PostDetailedActivity::class.java).apply {
                    putExtra("Uri", query)
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
            true
        }

        activityHomeScreenBinding.bottomNavigationView.setOnItemSelectedListener{
            activityHomeScreenBinding.topAppBar.requestFocus()
            when(it.itemId){
                R.id.miHome -> {

                    if( isValidDestination(R.id.homeFragment) and !navController.popBackStack(R.id.homeFragment, false)) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true

                }
                R.id.miCommunities -> {

                    if( isValidDestination(R.id.communitiesFragment) and !navController.popBackStack(
                            R.id.communitiesFragment, false)) {
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
                    if( isValidDestination(R.id.customFeedFragment) and !navController.popBackStack(
                            R.id.customFeedFragment, false)) {
                        navController.navigate(R.id.customFeedFragment)
                    }
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (activityHomeScreenBinding.drawerLayout.isOpen){
            activityHomeScreenBinding.drawerLayout.close()
        }
        else if (activityHomeScreenBinding.homeScreenSearchView.isShowing){
            activityHomeScreenBinding.homeScreenSearchView.hide()
        }
        else {
            super.onBackPressed()
            activityHomeScreenBinding.bottomNavigationView.selectedItemId = when(Navigation.findNavController(this,
                R.id.fragment_container_view
            ).currentDestination!!.id){
                R.id.homeFragment -> R.id.miHome
                R.id.communitiesFragment -> R.id.miCommunities
                R.id.customFeedFragment -> R.id.miCustomFeed
                else -> {0}
            }
        }
    }

//    private fun setCurrentFragment(fragment: Fragment) =
//        supportFragmentManager.beginTransaction().apply {
//            hide(current)
//            show(fragment)
//            commit()
//        }
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.nav_host_fragment, fragment)
//            commit()
//        }

    private fun isValidDestination(destination: Int) : Boolean{
        return destination != Navigation.findNavController(this, R.id.fragment_container_view).currentDestination!!.id
    }

    private fun openProfile(){
        Intent(this, UserProfileActivity::class.java).apply {
            putExtra("UserId", (application as Eden).userId)
            startActivity(this)
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        val currentUser = (application as Eden).userId
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.user_1 -> {
                    if (currentUser != 1){
                        (application as Eden).userId = 1
                        restart()
                    }
                }
                R.id.user_2 -> {
                    if (currentUser != 2){
                        (application as Eden).userId = 2
                        restart()
                    }
                }
                R.id.user_3 -> {
                    if (currentUser != 3){
                        (application as Eden).userId = 3
                        restart()
                    }
                }
            }
            false
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    private fun restart(){
        finish()
        startActivity(intent)
        overridePendingTransition(0, 1)
    }
}