package com.example.eden

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.eden.databinding.ActivityHomeScreenBinding
import com.example.eden.databinding.ActivityMainBinding
import com.example.eden.ui.ChatFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeScreenActivity: AppCompatActivity() {
    private lateinit var activityHomeScreenBinding: ActivityHomeScreenBinding
    private lateinit var navController : NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(application)
//        lifecycleScope.launch {
//            clearDB()
//        }

        activityHomeScreenBinding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(activityHomeScreenBinding.root)

        val homeFragment = HomeFragment()
        val communitiesFragment = CommunitiesFragment()


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()


//        val application = requireNotNull(this).application
//        val dataSource = AppDatabase.getDatabase(application).postDao()
//        val viewModelFactory = HomeViewModelFactory(dataSource, application)
//
//        Log.i("HomeFragment", AppDatabase.getDatabase(application).isOpen.toString())
//        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
//        Log.i("HomeFragment", homeViewModel.toString())

        activityHomeScreenBinding.fabPost.setOnClickListener {
            Intent(this, NewPostActivity::class.java).apply {
                startActivity(this)
            }
        }



        activityHomeScreenBinding.bottomNavigationView.setOnItemSelectedListener{

            when(it.itemId){
                R.id.miHome -> {
//                    replaceFragment(homeFragment)
//                    val navOptions = NavOptions.Builder()
//                        .setPopUpTo(R.id.homeFragment, true)
//                        .build()
//                    navController.navigate(R.id.homeFragment, null, navOptions)
                    if( isValidDestination(R.id.homeFragment) and !navController.popBackStack(R.id.homeFragment, false)) {
                        navController.navigate(R.id.homeFragment)
                    }
                    //TODO Change to Nested If so that if HOME is pressed again then scroll back to top!

                }
                R.id.miCommunities -> {
//                    replaceFragment(communitiesFragment)
                    if( isValidDestination(R.id.communitiesFragment) and !navController.popBackStack(R.id.communitiesFragment, false)) {
                        navController.navigate(R.id.communitiesFragment)
                    }

                }
                R.id.miCreatePost -> {}
                R.id.miChat -> {
                    if( isValidDestination(R.id.chatFragment) and !navController.popBackStack(R.id.chatFragment, false)) {
                        navController.navigate(R.id.chatFragment)
                    }
                }
                R.id.miInbox -> {
                    if( isValidDestination(R.id.inboxFragment ) and !navController.popBackStack(R.id.inboxFragment, false)) {
                        navController.navigate(R.id.inboxFragment)
                    }
                }

            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.i("This is a log message!")
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        activityHomeScreenBinding.bottomNavigationView.selectedItemId = when(Navigation.findNavController(this, R.id.nav_host_fragment).currentDestination!!.id){
            R.id.homeFragment -> R.id.miHome
            R.id.communitiesFragment -> R.id.miCommunities
            R.id.chatFragment -> R.id.miChat
            R.id.inboxFragment -> R.id.miInbox
            else -> {0}
        }
    }

    private fun isValidDestination(destination: Int) : Boolean{
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment).currentDestination!!.id
    }

//    private fun replaceFragment(fragment: Fragment){
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
//        fragmentTransaction.commit()

//        supportFragmentManager.beginTransaction().apply {
//            replace(activityHomeScreenBinding.navHostFragment.id, fragment).addToBackStack()
//            commit()
//        }
//    }

    private suspend fun clearDB(){
        withContext(Dispatchers.IO) {
            database.postDao().deleteAll()
        }
    }

}