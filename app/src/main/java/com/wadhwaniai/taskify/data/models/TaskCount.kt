package com.wadhwaniai.taskify.data.models

import androidx.room.ColumnInfo
import com.wadhwaniai.taskify.utils.TaskType

data class TaskCount(
    @ColumnInfo(name = "task_category")
    val taskType: TaskType,
    val count: Int
)
