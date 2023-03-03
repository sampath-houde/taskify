package com.wadhwaniai.taskify.ui.auth

import androidx.lifecycle.ViewModel
import com.wadhwaniai.taskify.data.repo.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo
): ViewModel() {

    fun isUserLoggedIn() = authRepo.isUserLoggedIn()
}