package com.wadhwaniai.taskify.ui.mainScreen.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wadhwaniai.taskify.data.models.entity.TaskEntity
import com.wadhwaniai.taskify.data.repo.TaskRepo
import com.wadhwaniai.taskify.utils.ErrorTYpe
import com.wadhwaniai.taskify.utils.NetworkUtils
import com.wadhwaniai.taskify.utils.Resource
import com.wadhwaniai.taskify.utils.TaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val taskRepo: TaskRepo,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    var task: TaskEntity? = null

    private val _taskState = MutableStateFlow<Resource<Unit>>(Resource.Empty())
    val taskState: StateFlow<Resource<Unit>> = _taskState

    private val _operation = MutableStateFlow<TaskState?>(null)
    val operation: StateFlow<TaskState?> = _operation


    fun startTask() = viewModelScope.launch {
        if (checkInternetBeforeCall()) {
            _taskState.emit(Resource.Loading())
            task?.let {
                it.state = TaskState.RUNNING
                _taskState.emit(taskRepo.updateTask(it))
                _operation.emit(TaskState.RUNNING)
            } ?: _taskState.emit(Resource.Error("Task is null"))
        }
    }

    fun pauseTask(timeLeft: Long) = viewModelScope.launch {
        if (checkInternetBeforeCall()) {
            _taskState.emit(Resource.Loading())
            task?.let {
                it.state = TaskState.PAUSED
                it.timeLeft = timeLeft
                _taskState.emit(taskRepo.updateTask(it))
                _operation.emit(TaskState.PAUSED)
            } ?: _taskState.emit(Resource.Error("Task is null"))
        }
    }

    fun stopTask(timeLeft: Long) = viewModelScope.launch {
        if (checkInternetBeforeCall()) {
            _taskState.emit(Resource.Loading())
            task?.let {
                it.state = TaskState.COMPLETED
                it.timeLeft = timeLeft
                _taskState.emit(taskRepo.updateTask(it))
                _operation.emit(TaskState.COMPLETED)
            } ?: _taskState.emit(Resource.Error("Task is null"))
        }
    }

    private suspend fun checkInternetBeforeCall(): Boolean {
        return if (networkUtils.checkInternetConnection())
            true
        else {
            _taskState.emit(Resource.Error(errorType = ErrorTYpe.NO_INTERNET))
            false
        }
    }
}