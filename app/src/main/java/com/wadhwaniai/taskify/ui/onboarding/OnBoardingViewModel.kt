package com.wadhwaniai.taskify.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wadhwaniai.taskify.data.repo.PreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(private val preferencesRepo: PreferencesRepo): ViewModel() {

    private val _navigateToAuth = MutableStateFlow(false)
    val navigateToAuth: StateFlow<Boolean> = _navigateToAuth

    fun onContinuePressed() = viewModelScope.launch {
        setOnBoardingComplete()
        _navigateToAuth.emit(true)
    }

    private suspend fun setOnBoardingComplete() = preferencesRepo.setOnBoardingComplete()



}