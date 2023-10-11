package com.example.eden

import android.util.Log
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {
    init {
        Log.i("Testing", "HomeViewModel Initialized!")
    }

    val postList = mutableListOf(Post("Title 1", false, "description test", 2),
        Post("Title 1", false, "description test 2", 10),
        Post("Title 1", true, "description test 3 aidnaosindoa aiondaoisndoaidna aiondoaisdnaoidn ainjgndkljfngd gkdljfngldingldkfgn difngdlofngdlofgind gfidlngldkgnldfkgndglkgfdndlkfgnldgkn difngldfkgndlfkngdlfigndoifgnd dfingldgniglfod", 20),
        Post("Title 1", false, "description test 4", 220),
        Post("Title 1", true, "description test 5", 40),
        Post("Title 1", true, "description test 6", 50),
        Post("Title 1", true, "description test 7", 270),
        Post("Title 1", false, "description test 8", 702),
        Post("Title 1", true, "description test 9", 1000),
        Post("Title 1", false, "description test 10", 1),
        Post("Title 1", false, "description test 11", 0),
        Post("Title 1", false, "description test 12", 5))
}