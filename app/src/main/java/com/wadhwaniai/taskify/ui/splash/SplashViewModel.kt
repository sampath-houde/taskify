package com.wadhwaniai.taskify.ui.splash

import androidx.lifecycle.ViewModel
import com.wadhwaniai.taskify.data.repo.PreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    //private val authRepo: AuthRepo,
    private val preferencesRepo: PreferencesRepo
) : ViewModel() {

    fun isOnBoardingComplete() = preferencesRepo.isOnBoardingComplete()

    //fun isUserLoggedIn() = authRepo.
}