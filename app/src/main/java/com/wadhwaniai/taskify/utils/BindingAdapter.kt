package com.wadhwaniai.taskify.utils

import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import coil.load
import com.bumptech.glide.Glide
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.data.models.entity.TaskEntity
import java.time.Duration
import java.util.*


@BindingAdapter("setLargeImage")
fun ImageView.setLargeImage(image: Int) {
    this.load(image) {
        crossfade(true)
    }
}

@BindingAdapter("setErrorImage")
fun ImageView.setErrorImage(uri: Int) {
    Glide.with(context)
        .load(uri)
        .into(this)
}

@BindingAdapter("setTaskDuration")
fun TextView.setTaskDuration(task: TaskEntity) {
    val timeLeft = if (task.state == TaskState.COMPLETED) task.duration else task.timeLeft
    text = timeLeft.formatDuration()
}

@BindingAdapter("setTaskDuration")
fun TextView.setDuration(timeLeft: Long) {
    text = timeLeft.formatDuration()
}

@BindingAdapter("setTimeLeft")
fun TextView.setTimeLeft(timeLeft: Long) {
    val duration = Duration.ofMillis(timeLeft)
    val seconds = duration.seconds
    text = String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60))
}

@BindingAdapter("setBackground")
fun TextView.setBackground(taskType: TaskType) {
    background = ContextCompat.getDrawable(context, taskType.tagBackground)
}

@BindingAdapter("setTagImage")
fun ImageView.setTagImage(taskType: TaskType) {
    setImageResource(taskType.imageId)
}

@BindingAdapter("setTaskState")
fun TextView.setTaskState(taskState: TaskState) {
    text = taskState.name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
}

@BindingAdapter("setTaskStateVisibility")
fun TextView.setTaskStateVisibility(taskState: TaskState) {
    isVisible = taskState == TaskState.COMPLETED || taskState == TaskState.PAUSED
}

@BindingAdapter("setUserProfileImage")
fun ImageView.setProfileImage(url: String) {
    val errorImage = R.drawable.avatar_male
    Glide.with(context)
        .load(url)
        .error(errorImage)
        .centerCrop()
        .into(this)
}

@BindingAdapter("setTaskTypeBg")
fun TextView.setTaskTypeBg(taskType: TaskType) {
    background = ContextCompat.getDrawable(context, taskType.tagBackground)
}

@BindingAdapter("setTaskTypeBackground")
fun LinearLayout.setTaskTypeBg(taskType: TaskType) {
    background = ContextCompat.getDrawable(context, taskType.tagBackground)
}

@BindingAdapter("setBarCount")
fun TextView.setBarCount(count: Int) {
    text = "$count"
}

