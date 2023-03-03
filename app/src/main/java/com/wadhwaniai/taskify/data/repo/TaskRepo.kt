package com.wadhwaniai.taskify.data.repo

import com.wadhwaniai.taskify.data.local.datasource.TaskDataSource
import com.wadhwaniai.taskify.data.models.entity.TaskEntity
import com.wadhwaniai.taskify.data.models.mapper.TaskMapper
import com.wadhwaniai.taskify.data.models.remote.TaskDTO
import com.wadhwaniai.taskify.data.remote.TasksOperations
import com.wadhwaniai.taskify.utils.ErrorTYpe
import com.wadhwaniai.taskify.utils.NetworkUtils
import com.wadhwaniai.taskify.utils.Resource
import com.wadhwaniai.taskify.utils.TaskState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class TaskRepo @Inject constructor(
    private val taskDataSource: TaskDataSource,
    private val firebaseDB: TasksOperations,
    private val taskMapper: TaskMapper,
    private val networkUtils: NetworkUtils
) {

    suspend fun addNewTask(taskEntity: TaskEntity) : Resource<Unit> = withContext(IO){
        if (!networkUtils.checkInternetConnection())
            return@withContext Resource.Error(
                message = "No internet",
                errorType = ErrorTYpe.NO_INTERNET
            )
        val taskDAO = taskMapper.toNetwork(taskEntity)
        val resource = firebaseDB.addNewTask(taskDAO)
        if (resource is Resource.Success) {
            insertTasksIntoDb(listOf(taskEntity))
            Resource.Success(message = "Tasks saved successfully")
        }
        else Resource.Error(message = resource.message, errorType = ErrorTYpe.UNKNOWN)
    }

    fun getAllUpcomingTasksOfToday() = taskDataSource.getAllTasksOfToday(
        state = TaskState.NOT_STARTED.name,
        todaysTime = getTodaysTime()
    ).flowOn(IO)

    fun getAllCompletedTasksOfToday() = taskDataSource.getAllTasksOfToday(
        state = TaskState.COMPLETED.name, todaysTime = getTodaysTime()
    ).flowOn(IO)

    fun getAllTasksSinceLastWeek() =
        taskDataSource.getTaskAfterGivenTime(getTimeOfLastWeek()).flowOn(IO)

    fun getTaskCounts() = taskDataSource.getTaskCounts().flowOn(IO)

    fun getTaskStates() = taskDataSource.getTaskStates().flowOn(IO)

    suspend fun updateTask(taskEntity: TaskEntity): Resource<Unit> = withContext(IO) {
        if (!networkUtils.checkInternetConnection())
            return@withContext Resource.Error(
                message = "No internet",
                errorType = ErrorTYpe.NO_INTERNET
            )
        val taskDAO = taskMapper.toNetwork(taskEntity)
        val resource = firebaseDB.updateTask(taskDAO)
        if (resource is Resource.Success) {
            updateTaskInDB(taskEntity)
            Resource.Success(message = "Tasks updated successfully")
        } else Resource.Error(message = resource.message, errorType = ErrorTYpe.UNKNOWN)
    }

    fun getRunningTask() = taskDataSource.getAllTasksOfToday(
        state = TaskState.RUNNING.name,
        todaysTime = getTodaysTime()
    ).flowOn(IO)

    fun getAllPausedTasks() = taskDataSource.getAllTasksOfToday(
        state = TaskState.PAUSED.name, todaysTime = getTodaysTime()
    ).flowOn(IO)

    suspend fun fetchAllTasks(email: String) : Resource<Unit> = withContext(IO) {
        if (!networkUtils.checkInternetConnection())
            return@withContext Resource.Error(
                message = "No internet",
                errorType = ErrorTYpe.NO_INTERNET
            )
        val resource = firebaseDB.fetchAllTasks(email)
        if (resource is Resource.Success) {
            saveAllNewDataInDb(resource.data ?: emptyList())
            Resource.Success(message = "Tasks fetched successfully")
        } else
            Resource.Error(message = resource.message, errorType = ErrorTYpe.UNKNOWN)
    }

    private fun getTodaysTime(): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        return cal.timeInMillis
    }

    private suspend fun saveAllNewDataInDb(taskDTO: List<TaskDTO>) {
        deleteAllTasks()
        val taskEntities = taskMapper.toDomainList(taskDTO)
        insertTasksIntoDb(taskEntities)
    }

    private suspend fun deleteAllTasks() = taskDataSource.deleteAllTasks()

    private suspend fun insertTasksIntoDb(taskEntities: List<TaskEntity>) {
        taskDataSource.insertTask(taskEntities)
    }

    private suspend fun updateTaskInDB(taskEntity: TaskEntity) =
        taskDataSource.updateTask(taskEntity)


    private fun getTimeOfLastWeek(): Long {
        val currentTime = System.currentTimeMillis()
        return currentTime - (7 * 24 * 60 * 60 * 1000)
    }
}