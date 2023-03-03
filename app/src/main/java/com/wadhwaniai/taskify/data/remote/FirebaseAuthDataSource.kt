package com.wadhwaniai.taskify.data.remote

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.wadhwaniai.taskify.data.models.remote.UserDTO
import com.wadhwaniai.taskify.utils.Resource
import com.wadhwaniai.taskify.utils.USER_COLLECTION_DB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val gson: Gson
    ) {

    fun logoutUser() = auth.signOut()

    fun getUserData() = auth.currentUser

    fun isUserLoggedIn() = auth.currentUser != null

    fun removeUser() {
        auth.currentUser?.delete()
    }

    suspend fun registerUser(email: String, username: String, password: String): Resource<Unit> =
        withContext(IO) {
            return@withContext try {
                auth.createUserWithEmailAndPassword(email, password).await()
                Resource.Success(message = "User signed up successfully")
            } catch (e: Exception) {
                Resource.Error(message = e.message.toString())
            }
        }

    suspend fun loginUser(email: String, password: String): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                auth.signInWithEmailAndPassword(email, password).await()
                Resource.Success(message = "User signed in successfully.")
            } catch (e: Exception) {
                Resource.Error(message = e.message.toString())
            }
        }

    suspend fun signInUsingCredential(credential: AuthCredential) = withContext(Dispatchers.IO) {
        return@withContext try {
            auth.signInWithCredential(credential).await()
            Resource.Success(
                data = auth.currentUser,
                message = "User signed up successfully"
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message.toString())
        }
    }

    suspend fun getGoogleAccount(data: Intent): Resource<GoogleSignInAccount> =
        withContext(Dispatchers.IO) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data).await()
                Resource.Success(data = task)
            } catch (e: Exception) {
                Resource.Error(message = e.message.toString())
            }
        }

    suspend fun getUserData(email: String) : Resource<UserDTO> = withContext(IO) {
        try {
            val query = db.collection(USER_COLLECTION_DB)
                .document(email)
                .get().await()
            val userData = gson.fromJson(query.data.toString(), UserDTO::class.java)

            return@withContext Resource.Success(data = userData)
        } catch (e: Exception) {
            return@withContext Resource.Error(message = "User does not exist")
        }
    }

    suspend fun saveNewUser(userDTO: UserDTO) : Resource<Unit> = withContext(IO) {
        try {
            db.collection(USER_COLLECTION_DB)
                .document(userDTO.email)
                .set(userDTO)

            db.collection(USER_COLLECTION_DB)
                .document(userDTO.email).id
            return@withContext Resource.Success(message = "User saved")
        } catch (e: Exception) {
            return@withContext Resource.Error(message = "User not saved")
        }
    }
}