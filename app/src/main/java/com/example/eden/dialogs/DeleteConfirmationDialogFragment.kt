package com.example.eden.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class DeleteConfirmationDialogFragment: DialogFragment() {
    private lateinit var listener: DeleteConfirmationDialogListener
    interface DeleteConfirmationDialogListener{
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Yes"
                ) { _, _ ->
                    listener.onDialogPositiveClick()
                }
                .setNegativeButton("No"
                ) { _, _ ->
                    listener.onDialogNegativeClick()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null!")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DeleteConfirmationDialogListener
        } catch (e: ClassCastException){
            throw ClassCastException((context.toString() +
                    " must implement DeleteConfirmationDialogListener!"))
        }
    }
}