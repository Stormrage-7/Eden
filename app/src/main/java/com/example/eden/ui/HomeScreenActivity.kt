package com.example.eden.ui

import android.app.SearchManager
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.get
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
import jp.wasabeef.blurry.Blurry
import timber.log.Timber

class HomeScreenActivity: AppCompatActivity(){
    private var isFabExpanded = false
    private val fromBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_fab)
    }
    private val toBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_fab)
    }
    private val rotateClockWiseFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate_clock_wise)
    }
    private val rotateAntiClockWiseFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate_anti_clock_wise)
    }
    private val fromBottomBgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim)
    }
    private val toBottomBgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim)
    }

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

        activityHomeScreenBinding.apply {
            createFab.setOnClickListener {
                if (isFabExpanded) { shrinkFab() }
                else { expandFab() }
            }

            createPostFab.setOnClickListener {
                shrinkFab()
                Intent(this@HomeScreenActivity, NewPostActivity::class.java).apply { startActivity(this) }
            }

            createCommunityFab.setOnClickListener {
                shrinkFab()
                Intent(this@HomeScreenActivity, NewCommunityActivity::class.java).apply { startActivity(this) }
            }

            transparentBackgroundView.setOnClickListener {
                if (isFabExpanded) shrinkFab()
            }
        }


        val headerView = activityHomeScreenBinding.navigationView.getHeaderView(0)
        val imageViewProfile = headerView.findViewById<ImageView>(R.id.imageViewNavDrawer)
        val textViewProfile = headerView.findViewById<TextView>(R.id.textViewNavDrawer)
        val btnChangeProfile = headerView.findViewById<Button>(R.id.changeProfileBtn)
        imageViewProfile.setOnClickListener {
            openProfile()
            activityHomeScreenBinding.drawerLayout.close()
        }
        textViewProfile.setOnClickListener {
            openProfile()
            activityHomeScreenBinding.drawerLayout.close()
        }
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

        setSupportActionBar(activityHomeScreenBinding.topAppBar)
        activityHomeScreenBinding.homeScreenSearchView.setupWithSearchBar(activityHomeScreenBinding.searchBar)
        activityHomeScreenBinding.searchBar.setNavigationOnClickListener {
            activityHomeScreenBinding.drawerLayout.open()
        }

        activityHomeScreenBinding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId){
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
        if (isFabExpanded){
            shrinkFab()
        }
        else if (activityHomeScreenBinding.drawerLayout.isOpen){
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

//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        if (ev?.action == MotionEvent.ACTION_DOWN){
//            if (isFabExpanded){
//                val outRect = Rect()
//                activityHomeScreenBinding.fabLayout.getGlobalVisibleRect(outRect)
//                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) shrinkFab()
//            }
//        }
//
//        return super.dispatchTouchEvent(ev)
//    }

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

    private fun shrinkFab(){

        activityHomeScreenBinding.apply {
            transparentBackgroundView.startAnimation(toBottomBgAnim)
            transparentBackgroundView.visibility = View.GONE
            createFab.startAnimation(rotateAntiClockWiseFabAnim)
            createPostFab.startAnimation(toBottomFabAnim)
            createCommunityFab.startAnimation(toBottomFabAnim)
            createPostFab.visibility = View.GONE
            createCommunityFab.visibility = View.GONE
            transparentBackgroundView.isClickable = false
        }

        isFabExpanded = !isFabExpanded
    }
    private fun expandFab(){

        activityHomeScreenBinding.apply {
            createPostFab.visibility = View.VISIBLE
            createCommunityFab.visibility = View.VISIBLE
            transparentBackgroundView.startAnimation(fromBottomBgAnim)
            createFab.startAnimation(rotateClockWiseFabAnim)
            createPostFab.startAnimation(fromBottomFabAnim)
            createCommunityFab.startAnimation(fromBottomFabAnim)
            transparentBackgroundView.isClickable = true
        }
        isFabExpanded = !isFabExpanded
    }
}