package com.wadhwaniai.taskify.ui.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wadhwaniai.taskify.data.models.entity.TaskEntity
import com.wadhwaniai.taskify.data.repo.AuthRepo
import com.wadhwaniai.taskify.data.repo.PreferencesRepo
import com.wadhwaniai.taskify.data.repo.TaskRepo
import com.wadhwaniai.taskify.utils.Resource
import com.wadhwaniai.taskify.utils.StopWatchFor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val taskRepo: TaskRepo,
    private val preferencesRepo: PreferencesRepo
): ViewModel() {

    private val _taskFetchState = MutableStateFlow<Resource<Unit>>(Resource.Empty())
    val taskFetchState: StateFlow<Resource<Unit>> = _taskFetchState

    private val _logout = MutableStateFlow(false)
    val shouldLogout: StateFlow<Boolean> = _logout

    fun isServiceRunning() = preferencesRepo.isServiceRunning()



    private val _runningTask = taskRepo.getRunningTask()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val runningTask: StateFlow<List<TaskEntity>> = _runningTask

    var stopWatchFor: StopWatchFor? = null
    var task: TaskEntity? = null

    val user = authRepo.getUserData()

    fun fetchAllTasks() = viewModelScope.launch {
        Timber.d("fetching all tasks")
        _taskFetchState.emit(Resource.Loading())
        user?.email?.let {
            taskRepo.fetchAllTasks(email = it)
        } ?: _taskFetchState.emit(Resource.Error(message = "Failed to get user information"))
    }

    fun onLogoutPressed() = viewModelScope.launch {
        authRepo.logoutUser()
        _logout.emit(true)
    }

    fun saveServiceStarted() = saveServiceState(true)

    fun saveServiceStopped() = saveServiceState(false)

    private fun saveServiceState(running: Boolean) = viewModelScope.launch {
        preferencesRepo.setServiceRunning(running)
    }

}