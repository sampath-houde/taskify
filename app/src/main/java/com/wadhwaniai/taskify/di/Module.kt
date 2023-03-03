package com.wadhwaniai.taskify.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.data.local.datasource.SharedPreferencesDataSource
import com.wadhwaniai.taskify.data.local.datasource.PreferencesDataSource
import com.wadhwaniai.taskify.data.local.room.TaskDAO
import com.wadhwaniai.taskify.data.local.room.TaskifyDatabase
import com.wadhwaniai.taskify.data.models.mapper.TaskMapper
import com.wadhwaniai.taskify.data.models.mapper.UserMapper
import com.wadhwaniai.taskify.data.remote.FirebaseTaskDataSource
import com.wadhwaniai.taskify.data.remote.TasksOperations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Module {

    @Provides
    @Singleton
    @Named("sharedPrefStore")
    fun providesDataStorePreferencesDataSource(sharedPreferences: SharedPreferences, gson: Gson): PreferencesDataSource =
        SharedPreferencesDataSource(sharedPreferences, gson)

    @Provides
    @Singleton
    fun providesTaskOperations(db: FirebaseFirestore, gson: Gson): TasksOperations =
        FirebaseTaskDataSource(gson, db)

    @Provides
    @Singleton
    fun providesFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun proviedesFirebaseDatabase() = Firebase.firestore

    @Provides
    @Singleton
    fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        with(context) {
            return getSharedPreferences(getString(R.string.app_shared_pref), Context.MODE_PRIVATE)
        }
    }

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return Gson()
    }

    @Provides
    fun providesContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun providesTaskifyDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, TaskifyDatabase::class.java, "TaskifyDatabase"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providesUserMapper(): UserMapper = UserMapper()

    @Provides
    @Singleton
    fun providesTaskMapper(): TaskMapper = TaskMapper()

    @Provides
    @Singleton
    fun providesTaskDao(taskifyDatabase: TaskifyDatabase): TaskDAO = taskifyDatabase.getTaskDao()

}