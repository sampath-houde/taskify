package com.wadhwaniai.taskify.data.repo

import com.wadhwaniai.taskify.data.local.datasource.PreferencesDataSource
import javax.inject.Inject
import javax.inject.Named

class PreferencesRepo @Inject constructor(
    @Named("sharedPrefStore") private val preferencesDataSource: PreferencesDataSource
) {
    suspend fun setOnBoardingComplete() = preferencesDataSource.setOnBoardingComplete()

    fun isOnBoardingComplete() = preferencesDataSource.isOnboardingComplete()

    fun isServiceRunning() = preferencesDataSource.isServiceRunning()

    suspend fun setServiceRunning(running: Boolean) = preferencesDataSource.setServiceRunning(running)
}