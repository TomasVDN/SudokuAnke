package com.frontend

import android.content.Context
import android.widget.Toast

class ToastMaker {
    companion object {
        fun showSaveConfirmationMessage(context: Context, message: String) {
            val duration = Toast.LENGTH_LONG

            val toast = Toast.makeText(context, message, duration)
            toast.show()
        }
    }
}