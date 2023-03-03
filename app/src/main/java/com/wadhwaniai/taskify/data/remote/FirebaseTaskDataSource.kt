package com.wadhwaniai.taskify.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.wadhwaniai.taskify.data.models.remote.TaskDTO
import com.wadhwaniai.taskify.utils.Resource
import com.wadhwaniai.taskify.utils.TASKS_COLLECTION_DB
import com.wadhwaniai.taskify.utils.toTask
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FirebaseTaskDataSource @Inject constructor(
    private val gson: Gson,
    db: FirebaseFirestore
) : TasksOperations {

    private val tasksDB = db.collection(TASKS_COLLECTION_DB)

    override suspend fun addNewTask(taskDTO: TaskDTO) : Resource<Unit> = withContext(IO) {
        with(tasksDB.document(taskDTO.task_id)) {
            this.set(taskDTO).addOnFailureListener {
                Timber.e(it)
            }

            if (this.id == taskDTO.task_id)
                return@withContext Resource.Success(message = "Task Saved")
            else
                return@withContext Resource.Error(message = "Task not saved")

        }
    }

    override suspend fun getAllTodaysTasks(state: String, todaysTime: Long) : Resource<List<TaskDTO>> {
        return try {
            val result = mutableListOf<TaskDTO>().apply {
                tasksDB.whereEqualTo("state", state)
                    .get()
                    .await().documents.forEach {
                        add(it.toTask(gson))
                    }
            }

            Resource.Success(data = result, message = "Tasks fetched")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(message = "Tasks not fetched")

        }
    }


    override suspend fun fetchAllTasks(email: String) : Resource<List<TaskDTO>> = withContext(IO) {
        try {
            val result = mutableListOf<TaskDTO>().apply {
                tasksDB.whereEqualTo("email", email)
                    .get().await().forEach {
                        add(it.toTask(gson))
                    }
                }
            return@withContext Resource.Success(data = result ,message = "Tasks fetched")
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Resource.Error(message = "Tasks not fetched")
        }

    }

    override suspend fun updateTask(taskDTO: TaskDTO): Resource<Unit> = withContext(IO) {
        with(tasksDB.document(taskDTO.task_id)) {
            this.set(taskDTO).addOnFailureListener {
                Timber.e(it)
            }

            if (this.id == taskDTO.task_id)
                return@withContext Resource.Success(message = "Task updated")
            else
                return@withContext Resource.Error(message = "Task not updated")
        }
    }

}