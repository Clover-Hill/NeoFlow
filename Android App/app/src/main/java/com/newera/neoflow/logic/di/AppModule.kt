package com.newera.neoflow.logic.di

import android.content.Context
import androidx.room.Room
import com.newera.neoflow.data.database.TodoDatabase
import com.newera.neoflow.logic.dao.TodoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependencies Injection within the scope of the whole APP
 * Make better tests
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
{

    @Provides
    @Singleton
    fun getTodoDatabase(@ApplicationContext context: Context): TodoDatabase =
        Room.databaseBuilder(context, TodoDatabase::class.java, "todo_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun getTodoDao(todoDatabase: TodoDatabase) : TodoDao =
        todoDatabase.getTodoDao()

}