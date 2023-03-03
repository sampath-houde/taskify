package com.wadhwaniai.taskify.utils

import android.content.Intent
import android.os.Build
import android.widget.Button
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import com.wadhwaniai.taskify.data.models.remote.TaskDTO
import timber.log.Timber
import java.io.Serializable
import java.time.Duration


fun Long.formatDuration(format: String = "%dhrs %02dmin %02dsec"): String {
    val duration = Duration.ofMillis(this)
    val seconds = duration.seconds
    return String.format(
        format,
        seconds / 3600,
        (seconds % 3600) / 60,
        (seconds % 60)
    )
}

fun Button.setUsable(currentFragment: Int, topLevelScreens: TopLevelScreens) {
    Timber.d("$currentFragment ${topLevelScreens.fragmentId}")
    Timber.d((currentFragment != topLevelScreens.fragmentId).toString())
    isEnabled = currentFragment != topLevelScreens.fragmentId
    alpha = if (currentFragment != topLevelScreens.fragmentId) 1F else 0.5F
}

fun DocumentSnapshot.toTask(gson: Gson) : TaskDTO{
    return gson.fromJson(this.data.toString(), TaskDTO::class.java)
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}