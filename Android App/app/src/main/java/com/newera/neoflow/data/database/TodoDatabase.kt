package com.newera.neoflow.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.newera.neoflow.data.models.TodoItem
import com.newera.neoflow.data.utils.Converters
import com.newera.neoflow.logic.dao.TodoDao

/**
 * Todo database
 * An abstract class for TodoDataBase
 * @constructor Create empty Todo database
 */
@Database(entities = [TodoItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TodoDatabase: RoomDatabase() {

    abstract fun getTodoDao(): TodoDao

}
