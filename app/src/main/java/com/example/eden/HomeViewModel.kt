package com.example.eden

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class HomeViewModel(val database: PostDao,
                    application: Application): AndroidViewModel(application) {
    init {
        Log.i("Testing", "HomeViewModel Initialized!")
    }

    val postList = mutableListOf(Post(title = "Title 1", containsImage = false, bodyText = "description test", voteCounter = 2),
        Post(title = "Title 1", containsImage = false, bodyText = "description test 2", voteCounter = 10),
        Post(title = "Title 1", containsImage = true, bodyText = "description test 3 aidnaosindoa aiondaoisndoaidna aiondoaisdnaoidn ainjgndkljfngd gkdljfngldingldkfgn difngdlofngdlofgind gfidlngldkgnldfkgndglkgfdndlkfgnldgkn difngldfkgndlfkngdlfigndoifgnd dfingldgniglfod", voteCounter = 20),
        Post(title = "Title 1", containsImage = false, bodyText = "description test 4", voteCounter = 220),
        Post(title = "Title 1", containsImage = true, bodyText = "description test 5", voteCounter = 40),
        Post(title = "Title 1", containsImage = true, bodyText = "description test 6", voteCounter = 50),
        Post(title = "Title 1", containsImage = true, bodyText = "description test 7", voteCounter = 270),
        Post(title = "Title 1", containsImage = false, bodyText = "description test 8", voteCounter = 702),
        Post(title = "Title 1", containsImage = true, bodyText = "description test 9", voteCounter = 1000),
        Post(title = "Title 1", containsImage = false, bodyText = "description test 10", voteCounter = 1),
        Post(title = "Title 1", containsImage = false, bodyText = "description test 11", voteCounter = 0),
        Post(title = "Title 1", containsImage = false, bodyText = "description test 12", voteCounter = 5))

}