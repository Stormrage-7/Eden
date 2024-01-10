package com.example.eden.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException
import kotlin.IllegalStateException

class DiscardChangesDialogFragment: DialogFragment() {
    internal lateinit var listener: DiscardChangesDialogListener
    interface DiscardChangesDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Are you sure you want discard changes and exit?")
                .setPositiveButton("Yes"
                ) { dialog, id ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton("No"
                ) { dialog, id ->
                    listener.onDialogNegativeClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null!")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DiscardChangesDialogListener
        } catch (e: ClassCastException){
            throw ClassCastException((context.toString() +
                    " must implement DiscardChangesDialogListener!"))
        }
    }
}