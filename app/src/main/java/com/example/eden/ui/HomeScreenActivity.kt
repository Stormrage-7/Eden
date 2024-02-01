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

class HomeScreenActivity: AppCompatActivity(),
    ConfirmationDialogFragment.ConfirmationDialogListener{
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

        activityHomeScreenBinding.addCommunityBtn.setOnClickListener {
            activityHomeScreenBinding.topAppBar.requestFocus()
            Intent(this, NewCommunityActivity::class.java).apply {
                startActivity(this)
            }
        }

        activityHomeScreenBinding.homeScreenSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                activityHomeScreenBinding.homeScreenSearchView.setQuery("", false)
                activityHomeScreenBinding.homeScreenSearchView.clearFocus()
                activityHomeScreenBinding.topAppBar.requestFocus()
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
            activityHomeScreenBinding.topAppBar.requestFocus()
            when(it.itemId){
                R.id.miHome -> {

                    if( isValidDestination(R.id.homeFragment) and !navController.popBackStack(R.id.homeFragment, false)) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                    //TODO Change to Nested If so that if HOME is pressed again then scroll back to top!

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
                else -> {false}
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.i("This is a log message!")
    }

    override fun onResume() {
        super.onResume()

        if(activityHomeScreenBinding.homeScreenSearchView.isFocused) {
            activityHomeScreenBinding.topAppBar.requestFocus()
            Log.i("OnResume","Inside focus")
        }
        Log.i("OnResume","Outside focus")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!activityHomeScreenBinding.topAppBar.isFocused){
            activityHomeScreenBinding.topAppBar.requestFocus()
        }
        else {
            super.onBackPressed()
            activityHomeScreenBinding.bottomNavigationView.selectedItemId = when(Navigation.findNavController(this,
                R.id.nav_host_fragment
            ).currentDestination!!.id){
                R.id.homeFragment -> R.id.miHome
                R.id.communitiesFragment -> R.id.miCommunities
                R.id.customFeedFragment -> R.id.miCustomFeed
                else -> {0}
            }
        }
    }

    private fun isValidDestination(destination: Int) : Boolean{
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment).currentDestination!!.id
    }

    override fun onDialogPositiveClick() {

    }

    override fun onDialogNegativeClick() {

    }

}