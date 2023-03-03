package com.wadhwaniai.taskify.ui.mainScreen.home.upComing

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wadhwaniai.taskify.data.repo.TaskRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    taskRepo: TaskRepo
) : ViewModel(), LifecycleObserver {

    val upComingTasks = taskRepo.getAllUpcomingTasksOfToday()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}