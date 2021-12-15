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

@Module
@InstallIn(SingletonComponent::class)
/**
 * Provide Dependency Injection for TodoDatabase and TodoDao
 *
 * @constructor Create empty App module
 */
object AppModule
{

    /**
     * Provide Dependency Injection for TodoDatabase
     *
     * @param context
     * @return
     */
    @Provides
    @Singleton
    fun getTodoDatabase(@ApplicationContext context: Context): TodoDatabase =
        Room.databaseBuilder(context, TodoDatabase::class.java, "todo_database")
            .fallbackToDestructiveMigration()
            .build()

    /**
     * Provide Dependency Injection for TodoDao
     *
     * @param todoDatabase : Informs that TodoDatabase is a dependency for TodoDao
     * @return
     */
    @Provides
    @Singleton
    fun getTodoDao(todoDatabase: TodoDatabase) : TodoDao =
        todoDatabase.getTodoDao()

}