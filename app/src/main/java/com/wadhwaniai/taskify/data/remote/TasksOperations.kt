package com.wadhwaniai.taskify.data.remote

import com.wadhwaniai.taskify.data.models.remote.TaskDTO
import com.wadhwaniai.taskify.utils.Resource

interface TasksOperations {

    suspend fun addNewTask(taskDTO: TaskDTO) : Resource<Unit>

    suspend fun getAllTodaysTasks(state: String, todaysTime: Long) : Resource<List<TaskDTO>>

    suspend fun fetchAllTasks(email: String) : Resource<List<TaskDTO>>

    suspend fun updateTask(taskDTO: TaskDTO) : Resource<Unit>

}