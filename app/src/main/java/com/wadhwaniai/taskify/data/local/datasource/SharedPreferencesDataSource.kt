package com.wadhwaniai.taskify.data.local.datasource

import android.content.SharedPreferences
import com.google.gson.Gson
import com.wadhwaniai.taskify.data.models.entity.UserEntity
import javax.inject.Inject

class SharedPreferencesDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
    ) : PreferencesDataSource {

    companion object {
        const val USER_SAVE_KEY = "user"
        const val SERVICE_KEY = "Service"
        const val ON_BOARDING_KEY = "OnBoarding"
    }

    override fun isOnboardingComplete(): Boolean {
        return sharedPreferences.getBoolean(ON_BOARDING_KEY, false)
    }

    override suspend fun setOnBoardingComplete() {
        sharedPreferences.edit().putBoolean(ON_BOARDING_KEY, true).apply()
    }

    override fun getUserData(): UserEntity? {
        val user = sharedPreferences.getString(USER_SAVE_KEY, null)
        return gson.fromJson(user, UserEntity::class.java)
    }

    override fun isServiceRunning() = sharedPreferences.getBoolean(SERVICE_KEY, false)


        override suspend fun saveUserData(userEntity: UserEntity) {
            val serializedUser =  gson.toJson(userEntity)
            sharedPreferences.edit().putString(USER_SAVE_KEY, serializedUser).apply()
        }

    override suspend fun removeUserData() = sharedPreferences.edit().remove(USER_SAVE_KEY).apply()

    override suspend fun setServiceRunning(running: Boolean) {
        sharedPreferences.edit().putBoolean(SERVICE_KEY, running).apply()
    }
}