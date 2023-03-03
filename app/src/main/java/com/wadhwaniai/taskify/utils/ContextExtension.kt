package com.wadhwaniai.taskify.utils

import android.content.Context
import android.widget.Toast
import androidx.datastore.preferences.preferencesDataStore

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}