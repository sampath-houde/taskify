package com.wadhwaniai.taskify.data.local.datasource

import com.wadhwaniai.taskify.data.local.room.TaskDAO
import com.wadhwaniai.taskify.data.models.entity.TaskEntity
import javax.inject.Inject

class TaskDataSource @Inject constructor(private val taskDAO: TaskDAO) {

    suspend fun insertTask(taskEntity: List<TaskEntity>) = taskDAO.insertTask(taskEntity)

    suspend fun updateTask(taskEntity: TaskEntity) = taskDAO.updateTask(taskEntity)

    suspend fun deleteTask(taskEntity: TaskEntity) = taskDAO.deleteTask(taskEntity)

    fun getTaskCounts() = taskDAO.getTasksByCount()

    fun getTaskAfterGivenTime(time: Long) = taskDAO.getTaskAfterGivenTime(time)

    fun getAllTasksOfToday(state: String, todaysTime: Long) =
        taskDAO.getAllTasksOfToday(state, todaysTime)

    fun getTaskStates() = taskDAO.getTaskStates()

    suspend fun deleteAllTasks() = taskDAO.deleteAllTasks()

}