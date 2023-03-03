package com.wadhwaniai.taskify.data.repo

import com.wadhwaniai.taskify.data.local.datasource.PreferencesDataSource
import com.wadhwaniai.taskify.data.models.mapper.UserMapper
import com.wadhwaniai.taskify.data.models.remote.UserDTO
import com.wadhwaniai.taskify.data.remote.FirebaseAuthDataSource
import com.wadhwaniai.taskify.utils.ErrorTYpe
import com.wadhwaniai.taskify.utils.NetworkUtils
import com.wadhwaniai.taskify.utils.Resource
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named


class AuthRepo @Inject constructor (
    @Named("sharedPrefStore") private val preferencesDataSource: PreferencesDataSource,
    private val networkUtils: NetworkUtils,
    private val authDataSource: FirebaseAuthDataSource,
    private val userMapper: UserMapper) {

    fun isUserLoggedIn() = authDataSource.isUserLoggedIn() && preferencesDataSource.getUserData() != null

    fun getUserData() = preferencesDataSource.getUserData()

    suspend fun logoutUser() {
        authDataSource.logoutUser()
        preferencesDataSource.removeUserData()
    }

    suspend fun loginUser(email: String, password: String): Resource<Unit> =
        withContext(Dispatchers.IO) {
            if (!networkUtils.checkInternetConnection()) {
                return@withContext Resource.Error(
                    message = "No Internet",
                    errorType = ErrorTYpe.NO_INTERNET
                )
            }
            val resource = authDataSource.loginUser(email, password)
            return@withContext getUserDataAfterLogin(resource, email)
        }

    suspend fun registerUser(email: String, username: String, password: String): Resource<Unit> =
        withContext(Dispatchers.IO) {
            if (!networkUtils.checkInternetConnection())
                return@withContext Resource.Error(
                    message = "No internet",
                    errorType = ErrorTYpe.NO_INTERNET
                )
            val resource = authDataSource.registerUser(email, username = username, password = password)
            if (resource is Resource.Error)
                return@withContext Resource.Error(message = resource.message)
            val userDTO = UserDTO(username = username, email = email)
            return@withContext storeUserDataAfterRegister(resource, userDTO)
        }

    private suspend fun<T> getUserDataAfterLogin(
        authResource: Resource<T>,
        email: String
    ) : Resource<Unit> {
        val firebaseDbUser = authDataSource.getUserData(email)
        if (firebaseDbUser is Resource.Error || authResource is Resource.Error) {
            if (firebaseDbUser is Resource.Error && authResource is Resource.Success) {
                if (firebaseDbUser.message == "User does not exist")
                    removeUser()
                else
                    logoutUser()
                return Resource.Error(message = firebaseDbUser.message)
            }
            return Resource.Error(message = authResource.message, errorType = ErrorTYpe.UNKNOWN)
        }
        preferencesDataSource.saveUserData(userMapper.toDomainModel(firebaseDbUser.data!!))
        return Resource.Success(message = "User logged in successfully")
    }

    private suspend fun <T> storeUserDataAfterRegister(
        resource: Resource<T>,
        userDTO: UserDTO,
    ): Resource<Unit> {
        val firebaseUser = authDataSource.saveNewUser(userDTO)
        if (firebaseUser is Resource.Error || resource is Resource.Error) {
            if (firebaseUser is Resource.Error && resource is Resource.Success) {
                removeUser()
            }
            return Resource.Error(message = resource.message, errorType = ErrorTYpe.UNKNOWN)
        }
        preferencesDataSource.saveUserData(userMapper.toDomainModel(userDTO))
        return Resource.Success(message = "User registered successfully")
    }

    private suspend fun removeUser() {
        authDataSource.removeUser()
        preferencesDataSource.removeUserData()
    }
}