package com.wadhwaniai.taskify.data.local.datasource

import com.wadhwaniai.taskify.data.models.entity.UserEntity


interface PreferencesDataSource {

    fun isOnboardingComplete(): Boolean

    suspend fun setOnBoardingComplete()

    fun getUserData() : UserEntity?

    fun isServiceRunning() : Boolean

    suspend fun saveUserData(userEntity: UserEntity)

    suspend fun removeUserData()

    suspend fun setServiceRunning(running: Boolean)
}